package com.lijukay.quotesAltDesign.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lijukay.quotesAltDesign.BuildConfig;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.adapter.InformationAdapter;
import com.lijukay.quotesAltDesign.item.InformationItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class Information extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    RequestQueue mRequestQueue;
    int versionCurrent, versionCode;
    private ArrayList<InformationItem> items;
    RecyclerView recyclerView;
    InformationAdapter adapter;
    SharedPreferences color, language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        color = getSharedPreferences("Colors", 0);

        language = getSharedPreferences("language", 0);  //TODO: ADD MULTILANGUAGE SUPPORT

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

        setContentView(R.layout.activity_information);

        recyclerView = findViewById(R.id.informationRV);
        recyclerView.setHasFixedSize(true);
        boolean tablet = getResources().getBoolean(R.bool.isTablet);
        if (tablet){
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        }
        items = new ArrayList<>();

        swipeRefreshLayout = findViewById(R.id.informationSRL);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                Toast.makeText(Information.this, getString(R.string.refresh_message), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Cache cache = mRequestQueue.getCache();
                    adapter.notifyDataSetChanged();
                    items.clear();
                    cache.clear();
                    parseJSON();
                }, 2000);
            }
        });


        versionCurrent = BuildConfig.VERSION_CODE;
        mRequestQueue = Volley.newRequestQueue(this);
        parseJSON();
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
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        mRequestQueue.add(requestPQ);
    }

}