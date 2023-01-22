package com.lijukay.quotesAltDesign.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.adapter.QuotesAdapter;
import com.lijukay.quotesAltDesign.adapter.wisdomAdapter;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.AllItem;
import com.lijukay.quotesAltDesign.item.wisdomItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class Person extends AppCompatActivity implements RecyclerViewInterface {
    private String authorP;
    private RecyclerView mRecyclerViewPQ;
    private ArrayList<AllItem> mPQItem;
    private ArrayList<wisdomItem> items;
    private wisdomAdapter adapter;
    private QuotesAdapter adapterAll;
    private RequestQueue mRequestQueuePQ;
    private String pQuotes, activity, type;
    private SwipeRefreshLayout swipeRefreshLayoutPQ;
    private SharedPreferences language;


    @SuppressLint({"NotifyDataSetChanged", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        language = getSharedPreferences("Language", 0);

        SharedPreferences color = getSharedPreferences("Colors", 0);

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

        setContentView(R.layout.activity_person);

        Intent intent = getIntent();

        type = intent.getStringExtra("type");

        authorP = intent.getStringExtra("author");

        activity = intent.getStringExtra("Activity");


        TextView author = findViewById(R.id.personname);
        author.setText(authorP);
        findViewById(R.id.setting).setOnClickListener(view -> startActivity(new Intent(Person.this, Settings.class)));

        mRecyclerViewPQ = findViewById(R.id.personRV);
        mRecyclerViewPQ.setHasFixedSize(true);
        boolean tablet = getResources().getBoolean(R.bool.isTablet);
        if (tablet){
            mRecyclerViewPQ.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            mRecyclerViewPQ.setLayoutManager(new LinearLayoutManager(this));
        }

        mPQItem = new ArrayList<>();
        items = new ArrayList<>();

        swipeRefreshLayoutPQ = findViewById(R.id.personSRL);
        swipeRefreshLayoutPQ.setOnRefreshListener(() -> {
            Toast.makeText(Person.this, getString(R.string.refresh_message), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayoutPQ.setRefreshing(false);

                if(activity.equals("quotes")) {
                    mPQItem.clear();
                    //mPQAdapter.notifyDataSetChanged();
                    adapterAll.notifyDataSetChanged();
                    parseJSONQuotes(type);
                } else if (activity.equals("wisdom")){
                    items.clear();
                    adapter.notifyDataSetChanged();
                    parseJSON(type);
                }

            }, 2000);
        });

        mRequestQueuePQ = Volley.newRequestQueue(this);
        if(activity.equals("quotes")){
            parseJSONQuotes(type);
        } else if (activity.equals("wisdom")){
            parseJSON(type);
        }

    }

    private void parseJSONQuotes(String type) {
        if (type.equals("author")){

            String url;

            if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")){
                url = "https://lijukay.github.io/Qwotable/author-de.json";
            } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")){
                url = "https://lijukay.github.io/Qwotable/author-en.json";
            } else {
                url = "https://lijukay.github.io/Qwotable/author-en.json";
            }



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

                            mPQItem.add(new AllItem(authorPQ, quotePQ, foundIn));
                        }

                        //mPQAdapter = new PersonAdapter(Person.this, mPQItem, this);
                        adapterAll = new QuotesAdapter(Person.this, mPQItem, this);
                        mRecyclerViewPQ.setAdapter(adapterAll);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        mRequestQueuePQ.add(requestPQ);
        } else if (type.equals("found in")){

            String url;

            if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")){
                url = "https://lijukay.github.io/Qwotable/found-in-de.json";
            } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")){
                url = "https://lijukay.github.io/Qwotable/found-in-en.json";
            } else {
                url = "https://lijukay.github.io/Qwotable/found-in-en.json";
            }


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

                                mPQItem.add(new AllItem(authorPQ, quotePQ, foundIn));
                            }

                            //mPQAdapter = new PersonAdapter(Person.this, mPQItem, this);
                            adapterAll = new QuotesAdapter(Person.this, mPQItem, this);
                            mRecyclerViewPQ.setAdapter(adapterAll);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);
            mRequestQueuePQ.add(requestPQ);
        }
    }

    private void parseJSON(String type) {

        String url;

        if (type.equals("found in")){
            if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")){
                url = "https://lijukay.github.io/Qwotable/wisdom-de.json";
            } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")){
                url = "https://lijukay.github.io/Qwotable/found-in-w-de.json";
            } else {
                url = "https://lijukay.github.io/Qwotable/found-in-w-en.json";
            }

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

                                items.add(new wisdomItem(author, quote, found_in, title));
                            }

                            adapter = new wisdomAdapter(this, items, this);
                            mRecyclerViewPQ.setAdapter(adapter); //I set the adapter of the RecyclerView to Adapter, that way I don't need to create an extra Adapter for Person.
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);
            mRequestQueuePQ.add(jsonObjectRequest);
        } else {
            if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")){
                url = "https://lijukay.github.io/Qwotable/wisdom-de.json";
            } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")){
                url = "https://lijukay.github.io/Qwotable/wisdom-en.json";
            } else {
                url = "https://lijukay.github.io/Qwotable/wisdom-en.json";
            }

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

                                items.add(new wisdomItem(author, quote, found_in, title));
                            }

                            adapter = new wisdomAdapter(this, items, this);
                            mRecyclerViewPQ.setAdapter(adapter); //I set the adapter of the RecyclerView to Adapter, that way I don't need to create an extra Adapter for Person.
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);
            mRequestQueuePQ.add(jsonObjectRequest);
        }

        //TODO: create found in for wisdom

    }


    @Override
    public void onItemClick(int position, String type) {
        //TODO: Log.w("Work required", "Create and show dialog")
    }
}