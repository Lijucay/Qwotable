package com.lijukay.quotesAltDesign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lijukay.quotesAltDesign.adapter.PersonAdapter;
import com.lijukay.quotesAltDesign.item.PersonItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Person extends AppCompatActivity implements RecyclerViewInterface{
    String authorP;
    TextView author, noItem;

    private RecyclerView mRecyclerViewPQ;
    private PersonAdapter mPQAdapter;
    private ArrayList<PersonItem> mPQItem;
    private RequestQueue mRequestQueuePQ;
    private String pQuotes, authorPQ, language;
    private SwipeRefreshLayout swipeRefreshLayoutPQ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Intent intent = getIntent();

        authorP = intent.getStringExtra("author");

        author = findViewById(R.id.personname);
        author.setText(authorP);

        findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Person.this, Settings.class));
            }
        });

        mRecyclerViewPQ = findViewById(R.id.personRV);
        mRecyclerViewPQ.setHasFixedSize(true);
        mRecyclerViewPQ.setLayoutManager(new LinearLayoutManager(this));

        mPQItem = new ArrayList<>();

        swipeRefreshLayoutPQ = findViewById(R.id.personSRL);
        swipeRefreshLayoutPQ.setOnRefreshListener(() -> {
            Toast.makeText(Person.this, "Refreshing", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayoutPQ.setRefreshing(false);
                mPQItem.clear();
                mPQAdapter.notifyDataSetChanged();
                parseJSON();
            }, 2000);
        });

        mRequestQueuePQ = Volley.newRequestQueue(this);
        //mRecyclerViewPQ.setVisibility(View.VISIBLE);
        parseJSON();

    }

    private void parseJSON() {
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

    @Override
    public void onItemClick(int position) {
        //TODO: Show Dialog
    }
}