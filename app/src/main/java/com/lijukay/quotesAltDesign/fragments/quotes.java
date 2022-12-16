package com.lijukay.quotesAltDesign.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lijukay.quotesAltDesign.activities.Person;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.adapter.AllAdapter;
import com.lijukay.quotesAltDesign.item.AllItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class quotes extends Fragment implements RecyclerViewInterface {

    private RecyclerView recyclerView;
    private AllAdapter adapter;
    private ArrayList<AllItem> items;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quotes, container, false);

        recyclerView = v.findViewById(R.id.quotesRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        items = new ArrayList<>();
        swipeRefreshLayout = v.findViewById(R.id.quotesSRL);
        swipeRefreshLayout.setOnRefreshListener(() -> {

            Toast.makeText(requireActivity(),getString(R.string.refresh_message), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                items.clear();
                adapter.notifyDataSetChanged();
                parseJSON();
            }, 2000);
        });
        requestQueue = Volley.newRequestQueue(requireContext());

        parseJSON();

        return v;
    }

    private void parseJSON() {
        String url = "https://lijukay.github.io/Qwotable/quotes-en.json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                jsonObject -> {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("AllQuotes");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String quote = object.getString("quoteAll");
                            String author = object.getString("authorAll");

                            items.add(new AllItem(author, quote));
                        }

                        adapter = new AllAdapter(getActivity(), items, this);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onItemClick(int position) {

            String url = "https://lijukay.github.io/Qwotable/quotes-en.json";


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    jsonObject -> {
                        try {
                            JSONArray jsonArrayP = jsonObject.getJSONArray("AllQuotes");

                            JSONObject object = jsonArrayP.getJSONObject(position);

                            String authorP = object.getString("authorAll");

                            Intent intent = new Intent(requireActivity(), Person.class);
                            intent.putExtra("author", authorP);
                            intent.putExtra("Activity", "quotes");
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);
            requestQueue.add(jsonObjectRequest);

    }

}