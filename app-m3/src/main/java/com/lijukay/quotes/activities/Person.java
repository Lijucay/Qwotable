package com.lijukay.quotes.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.elevation.SurfaceColors;
import com.lijukay.quotes.R;
import com.lijukay.quotes.adapter.QuotesAdapter;
import com.lijukay.quotes.adapter.WisdomAdapter;
import com.lijukay.quotes.interfaces.RecyclerViewInterface;
import com.lijukay.quotes.item.QuoteItem;
import com.lijukay.quotes.item.WisdomItem;
import com.lijukay.quotes.service.InternetService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Person extends AppCompatActivity implements RecyclerViewInterface {

    private String authorP;
    private RecyclerView recyclerView;
    private ArrayList<QuoteItem> mPQItem;
    private ArrayList<WisdomItem> items;
    private WisdomAdapter adapter;
    private QuotesAdapter adapterAll;
    private RequestQueue requestQueue;
    private String pQuotes, activity, type2;
    private SwipeRefreshLayout swipeRefreshLayout;
    private IntentFilter mIntentFilter;
    private boolean internet;
    private LinearLayout error;
    private TextView errorTitle, errorMessage;
    private SharedPreferences language;

    public static String BROAD_CAST_STRING_FOR_ACTION = "checkInternet";

    @SuppressLint({"NotifyDataSetChanged", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        language = getSharedPreferences("Language", 0);
        SharedPreferences colorPreference = getSharedPreferences("Colors", 0);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            setTheme(R.style.AppTheme);
        } else {
            switch (colorPreference.getString("color", "defaultOrDynamic")) {
                case "red":
                    setTheme(R.style.AppThemeRed);
                    break;
                case "pink":
                    setTheme(R.style.AppThemePink);
                    break;
                case "green":
                    setTheme(R.style.AppThemeGreen);
                    break;
                default:
                    setTheme(R.style.AppTheme);
                    break;
            }
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;
        float smallestWidth = Math.min(widthDp, heightDp);
        if (smallestWidth >= 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else if (smallestWidth < 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_person);

        error = findViewById(R.id.error);
        error.setVisibility(View.GONE);
        errorTitle = findViewById(R.id.titleError);
        errorMessage = findViewById(R.id.messageError);

        mIntentFilter = new IntentFilter();
        //------Action of this IntentFilter: Checking the internet------//
        mIntentFilter.addAction(BROAD_CAST_STRING_FOR_ACTION);
        //------Referring to the class where the service is written down and starting the service------//
        Intent serviceIntent = new Intent(this, InternetService.class);
        startService(serviceIntent);
        //------Checking if the Application is online------//
        internet = isOnline(getApplicationContext());

        Intent intent = getIntent();

        type2 = intent.getStringExtra("type");
        authorP = intent.getStringExtra("author");
        activity = intent.getStringExtra("Activity");

        MaterialToolbar materialToolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(materialToolbar);
        materialToolbar.setNavigationOnClickListener(v -> onBackPressed());
        materialToolbar.setTitle(authorP);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setTitle(authorP);

        int colorS = SurfaceColors.SURFACE_2.getColor(this);
        getWindow().setStatusBarColor(colorS);
        getWindow().setNavigationBarColor(colorS);

        recyclerView = findViewById(R.id.personRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.VISIBLE);
        boolean tablet = getResources().getBoolean(R.bool.isTablet);
        if (tablet) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        mPQItem = new ArrayList<>();
        items = new ArrayList<>();

        swipeRefreshLayout = findViewById(R.id.personSRL);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(Person.this, getString(R.string.toast_message_person), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                Cache cache = requestQueue.getCache();
                cache.clear();
                if (activity.equals("Quotes")) {
                    adapterAll.notifyDataSetChanged();
                    mPQItem.clear();
                } else if (activity.equals("wisdom")) {
                    adapter.notifyDataSetChanged();
                    items.clear();
                }
                checkInternet();

            }, 2000);
        });

        requestQueue = Volley.newRequestQueue(this);
        checkInternet();

    }

    private void parseJSONQuotes(String type) {
        if (type.equals("author")) {

            String url = "https://lijukay.github.io/Qwotable/author-" + language.getString("language", "en") + ".json";

            JsonObjectRequest requestPQ = new JsonObjectRequest(Request.Method.GET, url, null,
                    responsePQ -> {
                        try {
                            pQuotes = authorP;
                            JSONArray jsonArrayPQ = responsePQ.getJSONArray(pQuotes);


                            for (int a = 0; a < jsonArrayPQ.length(); a++) {
                                JSONObject pq = jsonArrayPQ.getJSONObject(a);

                                String quotePQ = pq.getString("quote");
                                String authorPQ = pq.getString("author");
                                String foundIn = pq.getString("found in");

                                mPQItem.add(new QuoteItem(authorPQ, quotePQ, foundIn));
                            }

                            adapterAll = new QuotesAdapter(Person.this, mPQItem, this);
                            recyclerView.setAdapter(adapterAll);
                        } catch (JSONException e) {
                            swipeRefreshLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            error.setVisibility(View.VISIBLE);
                            errorMessage.setText(getString(R.string.error_while_parsing_message_person));
                            //TODO: String
                            errorTitle.setText(getString(R.string.error_while_parsing_title));
                            findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);
            requestQueue.add(requestPQ);
        } else if (type.equals("found in")) {

            String url = "https://lijukay.github.io/Qwotable/found-in-" + language.getString("language", "en") + ".json";


            JsonObjectRequest requestPQ = new JsonObjectRequest(Request.Method.GET, url, null,
                    responsePQ -> {
                        try {
                            pQuotes = authorP;
                            JSONArray jsonArrayPQ = responsePQ.getJSONArray(pQuotes);


                            for (int a = 0; a < jsonArrayPQ.length(); a++) {
                                JSONObject pq = jsonArrayPQ.getJSONObject(a);

                                String quotePQ = pq.getString("quote");
                                String authorPQ = pq.getString("author");
                                String foundIn = pq.getString("found in");

                                mPQItem.add(new QuoteItem(authorPQ, quotePQ, foundIn));
                            }

                            //mPQAdapter = new PersonAdapter(Person.this, mPQItem, this);
                            adapterAll = new QuotesAdapter(Person.this, mPQItem, this);
                            recyclerView.setAdapter(adapterAll);
                        } catch (JSONException e) {
                            swipeRefreshLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            error.setVisibility(View.VISIBLE);
                            errorMessage.setText(getString(R.string.error_while_parsing_message_person));
                            errorTitle.setText(getString(R.string.error_while_parsing_title));
                            findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);
            requestQueue.add(requestPQ);
        }

    }

    private void parseJSON(String type) {
        String url;

        if (type.equals("found in")) {
            url = "https://lijukay.github.io/Qwotable/found-in-w-" + language.getString("language", "en") + ".json";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    jsonObject -> {
                        try {
                            pQuotes = authorP;
                            JSONArray jsonArray = jsonObject.getJSONArray(pQuotes);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String quote = object.getString("wisdom");
                                String author = object.getString("author");
                                String found_in = object.getString("found in");
                                String title = object.getString("title");

                                items.add(new WisdomItem(author, quote, found_in, title));
                            }

                            adapter = new WisdomAdapter(this, items, this);
                            recyclerView.setAdapter(adapter); //I set the adapter of the RecyclerView to Adapter, that way I don't need to create an extra Adapter for Person.
                        } catch (JSONException e) {
                            swipeRefreshLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            error.setVisibility(View.VISIBLE);
                            errorMessage.setText(getString(R.string.error_while_parsing_message_person));
                            //TODO: String
                            errorTitle.setText(getString(R.string.error_while_parsing_title));
                            findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);
            requestQueue.add(jsonObjectRequest);
        } else {
            url = "https://lijukay.github.io/Qwotable/wisdom-" + language.getString("language", "en") + ".json";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    jsonObject -> {
                        try {
                            pQuotes = authorP;
                            JSONArray jsonArray = jsonObject.getJSONArray(pQuotes);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String quote = object.getString("wisdom");
                                String author = object.getString("author");
                                String found_in = object.getString("found in");
                                String title = object.getString("title");

                                items.add(new WisdomItem(author, quote, found_in, title));
                            }

                            adapter = new WisdomAdapter(this, items, this);
                            recyclerView.setAdapter(adapter); //I set the adapter of the RecyclerView to Adapter, that way I don't need to create an extra Adapter for Person.
                        } catch (JSONException e) {
                            swipeRefreshLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            error.setVisibility(View.VISIBLE);
                            errorMessage.setText(getString(R.string.error_while_parsing_message_person));
                            errorTitle.setText(getString(R.string.error_while_parsing_title));
                            findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);
            requestQueue.add(jsonObjectRequest);
        }
    }

    public final BroadcastReceiver InternetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BROAD_CAST_STRING_FOR_ACTION)) {
                internet = intent.getStringExtra("online_status").equals("true");
            }
        }
    };

    public boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(InternetReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(InternetReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(InternetReceiver, mIntentFilter);
    }

    private void checkInternet() {
        if (!internet) {
            swipeRefreshLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
            errorTitle.setText(getString(R.string.no_internet_error_title));
            errorMessage.setText(getString(R.string.no_internet_error_message_person));
            findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
            if (activity.equals("Quotes")) {
                parseJSONQuotes(type2);
            } else if (activity.equals("wisdom")) {
                parseJSON(type2);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settingsMenu) {
            startActivity(new Intent(Person.this, Settings.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(int position, String type, MaterialButton mb) {
        if (activity.equals("Quotes")) {
            switch (type) {
                case "author":
                case "Found in":
                    break;
                case "copy": {
                    if (internet) {
                        String url;
                        if (type2.equals("author")) {
                            url = "https://lijukay.github.io/Qwotable/author-" + language.getString("language", "en") + ".json";
                        } else {
                            url = "https://lijukay.github.io/Qwotable/found-in-" + language.getString("language", "en") + ".json";
                        }

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                                jsonObject -> {
                                    try {
                                        pQuotes = authorP;
                                        JSONArray jsonArray = jsonObject.getJSONArray(pQuotes);
                                        JSONObject object = jsonArray.getJSONObject(position);

                                        String quote = object.getString("quote");
                                        String author = object.getString("author");

                                        copyText(quote + "\n\n~ " + author);
                                    } catch (JSONException e) {
                                        Toast.makeText(this, getString(R.string.error_while_parsing_toast_message_person), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }, Throwable::printStackTrace);
                        requestQueue.add(jsonObjectRequest);

                    } else {
                        Toast.makeText(this, getString(R.string.no_internet_toast_message), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }

    private void copyText(String quote) {
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Qwotable", quote);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.qwotable_copied_toast_message_person), Toast.LENGTH_SHORT).show();
    }
}