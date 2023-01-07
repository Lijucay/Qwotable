package com.lijukay.quotesAltDesign.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lijukay.quotesAltDesign.activities.Person;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.adapter.wisdomAdapter;
import com.lijukay.quotesAltDesign.item.wisdomItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class wisdom extends Fragment implements RecyclerViewInterface { //TODO: implements RecyclerViewInterface

    private RecyclerView recyclerView;
    private wisdomAdapter adapter;
    private ArrayList<wisdomItem> items;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wisdom, container, false);



        recyclerView = v.findViewById(R.id.wisdomRV);
        recyclerView.setHasFixedSize(true);
        boolean tablet = getResources().getBoolean(R.bool.isTablet);
        if (tablet){
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));

        }
        items = new ArrayList<>();
        swipeRefreshLayout = v.findViewById(R.id.wisdomSRL);
        swipeRefreshLayout.setOnRefreshListener(() -> {

            Toast.makeText(requireActivity(),getString(R.string.refresh_message), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                items.clear();
                Cache cache = requestQueue.getCache();
                cache.clear();
                adapter.notifyDataSetChanged();
                parseJSON();
            }, 2000);
        });
        requestQueue = Volley.newRequestQueue(requireContext());

        parseJSON();


        return v;


    }

    private void parseJSON() {
        String url = "https://lijukay.github.io/Qwotable/wisdom-en.json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                jsonObject -> {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("AllWisdom");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String quote = object.getString("wisdomAll");
                            String author = object.getString("authorAll");
                            String title = object.getString("titleAll");

                            items.add(new wisdomItem(author, quote, title));

                        }

                        adapter = new wisdomAdapter(getActivity(), items, this); //TODO: After implements RecyclerViewInterface chanfe null to this
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public void onItemClick(int position, String type) {

        if (type.equals("author")){


        String url = "https://lijukay.github.io/Qwotable/wisdom-en.json";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                jsonObject -> {
                    try {
                        JSONArray jsonArrayP = jsonObject.getJSONArray("AllWisdom");

                        JSONObject object = jsonArrayP.getJSONObject(position);

                        String authorP = object.getString("authorAll");

                        Intent intent = new Intent(requireActivity(), Person.class);
                        intent.putExtra("author", authorP);
                        intent.putExtra("Activity", "wisdom");
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        requestQueue.add(jsonObjectRequest);
        }

    }
}