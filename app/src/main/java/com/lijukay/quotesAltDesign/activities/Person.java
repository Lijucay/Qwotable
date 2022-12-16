package com.lijukay.quotesAltDesign.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.adapter.PersonAdapter;
import com.lijukay.quotesAltDesign.adapter.wisdomAdapter;
import com.lijukay.quotesAltDesign.item.PersonItem;
import com.lijukay.quotesAltDesign.item.wisdomItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Person extends AppCompatActivity implements RecyclerViewInterface {
    String authorP;
    TextView author;

    private RecyclerView mRecyclerViewPQ;
    private PersonAdapter mPQAdapter;
    private ArrayList<PersonItem> mPQItem;
    private ArrayList<wisdomItem> items;
    private wisdomAdapter adapter;
    private RequestQueue mRequestQueuePQ;
    private String pQuotes, activity;
    private SwipeRefreshLayout swipeRefreshLayoutPQ;




    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Intent intent = getIntent();

        authorP = intent.getStringExtra("author");
        activity = intent.getStringExtra("Activity");


        author = findViewById(R.id.personname);
        author.setText(authorP);

        findViewById(R.id.setting).setOnClickListener(view -> startActivity(new Intent(Person.this, Settings.class)));

        mRecyclerViewPQ = findViewById(R.id.personRV);
        mRecyclerViewPQ.setHasFixedSize(true);
        mRecyclerViewPQ.setLayoutManager(new LinearLayoutManager(this));

        mPQItem = new ArrayList<>();
        items = new ArrayList<>();

        swipeRefreshLayoutPQ = findViewById(R.id.personSRL);
        swipeRefreshLayoutPQ.setOnRefreshListener(() -> {
            Toast.makeText(Person.this, getString(R.string.refresh_message), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayoutPQ.setRefreshing(false);

                if(activity.equals("quotes")) {
                    mPQItem.clear();
                    mPQAdapter.notifyDataSetChanged();
                    parseJSONQuotes();
                } else if (activity.equals("wisdom")){
                    items.clear();
                    adapter.notifyDataSetChanged();
                    parseJSON();
                }

            }, 2000);
        });

        mRequestQueuePQ = Volley.newRequestQueue(this);
        //mRecyclerViewPQ.setVisibility(View.VISIBLE);
        if(activity.equals("quotes")){
            parseJSONQuotes();
        } else if (activity.equals("wisdom")){
            parseJSON();
        }

    }

    private void parseJSONQuotes() {
        String urlPQ = "https://lijukay.github.io/Quotes-M3/quotesEN.json";


        JsonObjectRequest requestPQ = new JsonObjectRequest(Request.Method.GET, urlPQ, null,
                responsePQ -> {
                    try {
                        pQuotes = authorP;
                        JSONArray jsonArrayPQ = responsePQ.getJSONArray(pQuotes);


                        for (int a = 0; a < jsonArrayPQ.length(); a++) {
                            JSONObject pq = jsonArrayPQ.getJSONObject(a);

                            String quotePQ = pq.getString("quotePQ");
                            String authorPQ = pq.getString("authorPQ");

                            mPQItem.add(new PersonItem(authorPQ, quotePQ));
                        }

                        mPQAdapter = new PersonAdapter(Person.this, mPQItem, this);
                        mRecyclerViewPQ.setAdapter(mPQAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        mRequestQueuePQ.add(requestPQ);
    }

    private void parseJSON() {
        String url = "https://lijukay.github.io/Qwotable/wisdom-en.json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                jsonObject -> {
                    try {
                        pQuotes = authorP;
                        JSONArray jsonArray = jsonObject.getJSONArray(pQuotes);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String quote = object.getString("wisdom");
                            String author = object.getString("author");
                            String title = object.getString("titleAll");

                            items.add(new wisdomItem(author, quote, title));
                        }

                        adapter = new wisdomAdapter(this, items, this);
                        mRecyclerViewPQ.setAdapter(adapter); //I set the adapter of the RecyclerView to Adapter, that way I don't need to create an extra Adapter for Person.
                        //Todo: Set mRecyclerViewPQ adapter of quotes to quotes adapter
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        mRequestQueuePQ.add(jsonObjectRequest);
    }


    @Override
    public void onItemClick(int position) {
        //TODO: Show Dialog
    }
}