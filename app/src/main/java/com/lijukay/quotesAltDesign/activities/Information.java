package com.lijukay.quotesAltDesign.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.elevation.SurfaceColors;
import com.lijukay.quotesAltDesign.BuildConfig;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.adapter.InformationAdapter;
import com.lijukay.quotesAltDesign.item.InformationItem;
import com.lijukay.quotesAltDesign.service.InternetService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Information extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RequestQueue mRequestQueue;
    private int versionCurrent, versionCode;
    private ArrayList<InformationItem> items;
    private RecyclerView recyclerView;
    private InformationAdapter adapter;
    private boolean internet;
    private LinearLayout error;
    private IntentFilter mIntentFilter;
    private TextView errorTitle, errorMessage;

    public static String BroadCastStringForAction = "checkInternet";
    public SharedPreferences color, language;



    @SuppressLint({"NotifyDataSetChanged", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        language = getSharedPreferences("language", 0); //Todo: Parse Information in other languages

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
        }
        else if (smallestWidth < 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        color = getSharedPreferences("Colors", 0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            setTheme(R.style.AppTheme);
        } else {
            switch (color.getString("color", "red")){
                case "red":
                    setTheme(R.style.AppTheme);
                    break;
                case "pink":
                    setTheme(R.style.AppThemePink);
                    break;
                case "green":
                    setTheme(R.style.AppThemeGreen);
                    break;
            }
        }

        setContentView(R.layout.activity_information);

        int color = SurfaceColors.SURFACE_2.getColor(this);
        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);
        boolean tablet = getResources().getBoolean(R.bool.isTablet);
        Intent serviceIntent = new Intent(this, InternetService.class);

        setSupportActionBar(materialToolbar);
        materialToolbar.setNavigationOnClickListener(v -> onBackPressed());

        getWindow().setStatusBarColor(color);
        getWindow().setNavigationBarColor(color);

        error = findViewById(R.id.error);
        error.setVisibility(View.GONE);
        errorTitle = findViewById(R.id.titleError);
        errorMessage = findViewById(R.id.messageError);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        startService(serviceIntent);
        internet = isOnline(getApplicationContext());

        recyclerView = findViewById(R.id.informationRV);
        if (tablet){
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
        recyclerView.setHasFixedSize(true);

        items = new ArrayList<>();

        swipeRefreshLayout = findViewById(R.id.informationSRL);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(Information.this, getString(R.string.toast_message_information), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                Cache cache = mRequestQueue.getCache();
                adapter.notifyDataSetChanged();
                items.clear();
                cache.clear();
                parseJSON();
            }, 2000);
        });

        versionCurrent = BuildConfig.VERSION_CODE;
        mRequestQueue = Volley.newRequestQueue(this);

        checkInternet();
    }

    private void checkInternet(){

        if (!internet){
            swipeRefreshLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
            errorTitle.setText(getString(R.string.no_internet_error_title));
            //Todo: String
            errorMessage.setText(getString(R.string.no_internet_message_information));
            //Todo: String
            findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
            parseJSON();
        }
    }

    private void parseJSON() {

        String urlPQ = "https://lijukay.github.io/Qwotable/information-en.json";

        JsonObjectRequest requestPQ = new JsonObjectRequest(Request.Method.GET, urlPQ, null,
                responsePQ -> {
                    try {
                        JSONArray jsonArrayPQ = responsePQ.getJSONArray("Information");

                        for (int i = 0; i < jsonArrayPQ.length(); i++){
                            JSONObject pq = jsonArrayPQ.getJSONObject(i);

                            versionCode = pq.getInt("valuable for version");
                            String title = pq.getString("title");
                            String message = pq.getString("message");
                            String date = pq.getString("date of creation");

                            if (versionCode >= versionCurrent || versionCode == 0){
                                items.add(new InformationItem(title, message, date));
                            }
                        }

                        adapter = new InformationAdapter(Information.this, items);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {

                        swipeRefreshLayout.setVisibility(View.GONE);

                        recyclerView.setVisibility(View.GONE);

                        error.setVisibility(View.VISIBLE);

                        errorMessage.setText(getString(R.string.error_while_parsing_message_information));

                        errorTitle.setText(getString(R.string.error_while_parsing_title));

                        findViewById(R.id.retry).setOnClickListener(v -> checkInternet());

                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        mRequestQueue.add(requestPQ);
    }

    public final BroadcastReceiver InternetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadCastStringForAction)) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settingsMenu){
            startActivity(new Intent(Information.this, Settings.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}