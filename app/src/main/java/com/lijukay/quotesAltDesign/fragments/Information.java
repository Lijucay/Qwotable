package com.lijukay.quotesAltDesign.fragments;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lijukay.quotesAltDesign.BuildConfig;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.activities.MainActivity;
import com.lijukay.quotesAltDesign.adapter.InformationAdapter;
import com.lijukay.quotesAltDesign.item.InformationItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Information extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RequestQueue mRequestQueue;
    private int versionCurrent, versionCode;
    private ArrayList<InformationItem> items;
    private RecyclerView recyclerView;
    private InformationAdapter adapter;
    private boolean internet;
    private IntentFilter mIntentFilter;
    private LinearLayout error;
    private TextView errorTitle, errorMessage;

    public static String BroadCastStringForAction = "checkInternet";
    //public SharedPreferences language;

    View v;



    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //language = requireContext().getSharedPreferences("language", 0); //Todo: Parse Information in other languages

        v = inflater.inflate(R.layout.fragment_information, container, false);

        error = v.findViewById(R.id.error);
        error.setVisibility(View.GONE);
        errorTitle = v.findViewById(R.id.titleError);
        errorMessage = v.findViewById(R.id.messageError);


        internet = ((MainActivity) requireActivity()).isOnline(requireContext().getApplicationContext());

        boolean tablet = getResources().getBoolean(R.bool.isTablet);

        recyclerView = v.findViewById(R.id.informationRV);
        if (tablet){
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        }
        recyclerView.setHasFixedSize(true);

        items = new ArrayList<>();

        swipeRefreshLayout = v.findViewById(R.id.informationSRL);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(requireContext(), getString(R.string.toast_message_information), Toast.LENGTH_SHORT).show();
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
        mRequestQueue = Volley.newRequestQueue(requireContext());

        checkInternet();

        return v;
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
            v.findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
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

                        adapter = new InformationAdapter(requireContext(), items);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {

                        swipeRefreshLayout.setVisibility(View.GONE);

                        recyclerView.setVisibility(View.GONE);

                        error.setVisibility(View.VISIBLE);

                        errorMessage.setText(getString(R.string.error_while_parsing_message_information));

                        errorTitle.setText(getString(R.string.error_while_parsing_title));

                        v.findViewById(R.id.retry).setOnClickListener(v -> checkInternet());

                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        mRequestQueue.add(requestPQ);
    }
}