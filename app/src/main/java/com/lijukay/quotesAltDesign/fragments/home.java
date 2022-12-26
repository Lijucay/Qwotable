package com.lijukay.quotesAltDesign.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lijukay.quotesAltDesign.BuildConfig;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.activities.Information;
import com.lijukay.quotesAltDesign.adapter.InformationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class home extends Fragment {

    int versionCode, versionCurrent, versionB;
    RequestQueue mRequestQueue;
    View v;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences beta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = v.findViewById(R.id.homeSRL);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(requireContext(), getString(R.string.refresh_message), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                Cache cache = mRequestQueue.getCache();
                cache.clear();

                parseJSON();
            }, 2000);
        });

        beta = requireActivity().getSharedPreferences("Beta", 0);


        versionCurrent = BuildConfig.VERSION_CODE;
        mRequestQueue = Volley.newRequestQueue(requireContext());
        parseJSON();

        v.findViewById(R.id.information_card).setOnClickListener(v -> startActivity(new Intent(getActivity(), Information.class)));

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
                        versionB = pq.getInt("versionsCodeBeta");
                        if (versionCode > versionCurrent){
                            v.findViewById(R.id.updateCard).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.updateCardBeta).setVisibility(View.GONE);

                        } else if (versionB > versionCurrent && beta.getBoolean("beta", false)){
                            v.findViewById(R.id.updateCard).setVisibility(View.GONE);
                            v.findViewById(R.id.updateCardBeta).setVisibility(View.VISIBLE);
                        } else {
                            v.findViewById(R.id.updateCard).setVisibility(View.GONE);
                            v.findViewById(R.id.updateCardBeta).setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        mRequestQueue.add(requestPQ);
    }
}