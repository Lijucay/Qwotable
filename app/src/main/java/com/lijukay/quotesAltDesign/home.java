package com.lijukay.quotesAltDesign;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class home extends Fragment {

    int versionCode, versionCurrent;
    RequestQueue mRequestQueue;
    View v;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = v.findViewById(R.id.homeSRL);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(requireContext(), "Refreshing", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                Cache cache = mRequestQueue.getCache();
                cache.clear();
                parseJSON();
            }, 2000);
        });


        versionCurrent = BuildConfig.VERSION_CODE;
        mRequestQueue = Volley.newRequestQueue(requireContext());
        parseJSON();

        return v;

    }

    private void parseJSON() {
        String urlPQ = "https://lijukay.github.io/PrUp/prUp.json";


        JsonObjectRequest requestPQ = new JsonObjectRequest(Request.Method.GET, urlPQ, null,
                responsePQ -> {
                    try {
                        JSONArray jsonArrayPQ = responsePQ.getJSONArray("Qwotable");

                        JSONObject pq = jsonArrayPQ.getJSONObject(0);

                        versionCode = pq.getInt("versionsCode");
                        Log.e("Update", versionCode+"");
                        if (versionCode > versionCurrent){
                            v.findViewById(R.id.updateCard).setVisibility(View.VISIBLE);
                        } else {
                            v.findViewById(R.id.updateCard).setVisibility(View.GONE);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        mRequestQueue.add(requestPQ);
    }
}