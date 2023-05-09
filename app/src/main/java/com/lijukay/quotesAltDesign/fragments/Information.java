package com.lijukay.quotesAltDesign.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
    private LinearLayout error;
    private TextView errorTitle, errorMessage, message;
    private View layout;
    //public SharedPreferences language;

    private View v;

    @SuppressLint({"SourceLockedOrientationActivity", "NotifyDataSetChanged", "InflateParams"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_information, container, false);

        layout = LayoutInflater.from(requireContext()).inflate(R.layout.toast_view, null);
        message = layout.findViewById(R.id.message);

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
            message.setText(R.string.toast_message_information);
            Toast toast = new Toast(requireContext().getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            //Toast.makeText(requireContext(), getString(R.string.toast_message_information), Toast.LENGTH_SHORT).show();
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

        if (!tablet) ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            recyclerView.setPadding(0,0,0,insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        }); else ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            recyclerView.setPadding(0,insets.top,0,insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        ViewCompat.setOnApplyWindowInsetsListener(v.findViewById(R.id.error), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.bottomMargin = insets.bottom;
            v.setLayoutParams(mlp);

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });


        return v;
    }

    private void checkInternet(){

        if (!internet){
            swipeRefreshLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
            errorTitle.setText(getString(R.string.no_internet_error_title));
            errorMessage.setText(getString(R.string.no_internet_message_information));
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