package com.lijukay.quotesAltDesign.fragments;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.activities.MainActivity;
import com.lijukay.quotesAltDesign.activities.Person;
import com.lijukay.quotesAltDesign.adapter.wisdomAdapter;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.wisdomItem;
import com.lijukay.quotesAltDesign.service.InternetService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


public class wisdom extends Fragment implements RecyclerViewInterface {

    private RecyclerView recyclerView;
    private wisdomAdapter adapter;
    private ArrayList<wisdomItem> items;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences language;
    private LinearLayout error;
    public static String BroadCastStringForAction = "checkInternet";
    private IntentFilter mIntentFilter;
    boolean internet;
    View v;
    private TextView errorMessage, errorTitle;
    private LinearProgressIndicator progressIndicator;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        language = requireActivity().getSharedPreferences("Language", 0);
        v = inflater.inflate(R.layout.fragment_wisdom, container, false);

        progressIndicator = v.findViewById(R.id.progress);
        progressIndicator.setVisibility(View.GONE);

        errorTitle = v.findViewById(R.id.titleError);
        errorMessage = v.findViewById(R.id.messageError);

        internet = ((MainActivity) requireActivity()).isOnline(requireActivity().getApplicationContext());


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

            Toast.makeText(requireActivity(), getString(R.string.toast_message_wisdom), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                items.clear();
                Cache cache = requestQueue.getCache();
                cache.clear();
                adapter.notifyDataSetChanged();
                checkInternet();
            }, 2000);
        });
        requestQueue = Volley.newRequestQueue(requireContext());

        error = v.findViewById(R.id.error);
        error.setVisibility(View.GONE);

        checkInternet();

        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            recyclerView.setPadding(0,0,0,insets.bottom);

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

    private void parseJSON() {
        progressIndicator.setVisibility(View.VISIBLE);

        String url;

        url = "https://lijukay.github.io/Qwotable/wisdom-" + language.getString("language", "en") + ".json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                jsonObject -> {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("Wisdom");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String wisdom = object.getString("wisdom");
                            String author = object.getString("author");
                            String foundIn = object.getString("found in");
                            String title = object.getString("title");

                            items.add(new wisdomItem(author, wisdom, foundIn, title));

                        }

                        adapter = new wisdomAdapter(getActivity(), items, this);
                        recyclerView.setAdapter(adapter);
                        progressIndicator.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        swipeRefreshLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        error.setVisibility(View.VISIBLE);
                        errorMessage.setText(getString(R.string.error_while_parsing_wisdom));
                        errorTitle.setText(getString(R.string.error_while_parsing_title));
                        v.findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public void onItemClick(int position, String type) {
        switch (type) {
            case "author": {
                String url = "https://lijukay.github.io/Qwotable/wisdom-" + language.getString("language", "en") + ".json";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        jsonObject -> {
                            try {
                                JSONArray jsonArrayP = jsonObject.getJSONArray("Wisdom");

                                JSONObject object = jsonArrayP.getJSONObject(position);

                                String authorP = object.getString("author");

                                Intent intent = new Intent(requireActivity(), Person.class);
                                intent.putExtra("author", authorP);
                                intent.putExtra("type", "author");
                                intent.putExtra("Activity", "wisdom");
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, Throwable::printStackTrace);
                requestQueue.add(jsonObjectRequest);
                break;
            }
            case "found in": {
                String url = "https://lijukay.github.io/Qwotable/wisdom-" + language.getString("language", "en") + ".json";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        jsonObject -> {
                            try {
                                JSONArray jsonArrayP = jsonObject.getJSONArray("Wisdom");

                                JSONObject object = jsonArrayP.getJSONObject(position);

                                String authorP = object.getString("found in");

                                Intent intent = new Intent(requireActivity(), Person.class);
                                intent.putExtra("author", authorP);
                                intent.putExtra("type", "found in");
                                intent.putExtra("Activity", "wisdom");
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, Throwable::printStackTrace);
                requestQueue.add(jsonObjectRequest);
                break;
            }
            case "copy": {
                if (internet){
                    String url = "https://lijukay.github.io/Qwotable/wisdom-" + language.getString("language", "en") + ".json";

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            jsonObject -> {
                                try {
                                    JSONArray jsonArray = jsonObject.getJSONArray("Wisdom");
                                    JSONObject object = jsonArray.getJSONObject(position);
                                    String wisdom = object.getString("wisdom");
                                    String author = object.getString("author");
                                    String title = object.getString("title");

                                    copyText(title + "\n\n" + wisdom + "\n\n" + author);
                                } catch (JSONException e) {
                                    Toast.makeText(requireContext(), getString(R.string.error_while_parsing_wisdom_toast_message), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }, Throwable::printStackTrace);
                    requestQueue.add(jsonObjectRequest);
                } else {
                    Toast.makeText(requireContext(), getString(R.string.no_internet_toast_message), Toast.LENGTH_SHORT).show();

                }
                break;
            }
        }
    }

    private void copyText(String wisdom) {
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Wisdom", wisdom);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), "Qwotable copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void checkInternet(){
        if (!internet){
            swipeRefreshLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
            errorTitle.setText(getString(R.string.no_internet_error_title));
            errorMessage.setText(getString(R.string.no_internet_error_message_wisdom));
            v.findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
            parseJSON();
        }
    }
}