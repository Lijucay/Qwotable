package com.lijukay.quotesAltDesign.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Cache;
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
import java.util.Locale;
import java.util.Objects;

public class quotes extends Fragment implements RecyclerViewInterface {

    private RecyclerView recyclerView;
    private AllAdapter adapter;
    private ArrayList<AllItem> items;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout error;
    private SharedPreferences language;
    View v;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        language = requireActivity().getSharedPreferences("Language", 0);

        v = inflater.inflate(R.layout.fragment_quotes, container, false);

        recyclerView = v.findViewById(R.id.quotesRV);
        recyclerView.setHasFixedSize(true);
        boolean tablet = getResources().getBoolean(R.bool.isTablet);
        if (tablet){
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));

        }
        error = v.findViewById(R.id.error);
        error.setVisibility(View.GONE);

        items = new ArrayList<>();
        swipeRefreshLayout = v.findViewById(R.id.quotesSRL);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setOnRefreshListener(() -> {

            Toast.makeText(requireActivity(),getString(R.string.refresh_message), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                Cache cache = requestQueue.getCache();
                cache.clear();
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
        String url;
        if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")){
            url = "https://lijukay.github.io/Qwotable/quotes-de.json";
        } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")){
            url = "https://lijukay.github.io/Qwotable/quotes-en.json";
        } else {
            url = "https://lijukay.github.io/Qwotable/quotes-en.json";
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                jsonObject -> {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("Quotes");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String quote = object.getString("quote");
                            String author = object.getString("author");
                            String foundIn = object.getString("found in");

                            items.add(new AllItem(author, quote, foundIn));
                        }

                        adapter = new AllAdapter(getActivity(), items, this);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        swipeRefreshLayout.setVisibility(View.GONE);
                        error.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onItemClick(int position, String type) {


            if (type.equals("author")){
                String url;

                if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")){
                    url = "https://lijukay.github.io/Qwotable/quotes-de.json";
                } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")){
                    url = "https://lijukay.github.io/Qwotable/quotes-en.json";
                } else {
                    url = "https://lijukay.github.io/Qwotable/quotes-en.json";
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        jsonObject -> {
                            try {
                                JSONArray jsonArrayP = jsonObject.getJSONArray("Quotes");

                                JSONObject object = jsonArrayP.getJSONObject(position);

                                String authorP = object.getString("author");

                                Intent intent = new Intent(requireActivity(), Person.class);
                                intent.putExtra("author", authorP);
                                intent.putExtra("type", "author");
                                intent.putExtra("Activity", "quotes");
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, Throwable::printStackTrace);
                requestQueue.add(jsonObjectRequest);

            } else if (type.equals("Found in")){
                String url;

                if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")){
                    url = "https://lijukay.github.io/Qwotable/quotes-de.json";
                } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")){
                    url = "https://lijukay.github.io/Qwotable/quotes-en.json";
                } else {
                    url = "https://lijukay.github.io/Qwotable/quotes-en.json";
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        jsonObject -> {
                            try {
                                JSONArray jsonArrayP = jsonObject.getJSONArray("Quotes");

                                JSONObject object = jsonArrayP.getJSONObject(position);

                                String authorP = object.getString("found in");

                                Intent intent = new Intent(requireActivity(), Person.class);
                                intent.putExtra("author", authorP);
                                intent.putExtra("type", "found in");
                                intent.putExtra("Activity", "quotes");
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }, Throwable::printStackTrace);
                requestQueue.add(jsonObjectRequest);

        }

    }

}