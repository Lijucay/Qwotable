package com.lijukay.quotesAltDesign.fragments;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.Manifest;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
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
import com.lijukay.quotesAltDesign.adapter.QuotesAdapter;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.AllItem;
import com.lijukay.quotesAltDesign.service.InternetService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class quotes extends Fragment implements RecyclerViewInterface {

    private RecyclerView recyclerView;
    private QuotesAdapter adapter;
    private ArrayList<AllItem> items;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout error;
    private SharedPreferences language;
    boolean internet;
    private View v, layout;
    private TextView errorMessage, errorTitle, message;
    boolean tablet;
    private LinearProgressIndicator progressIndicator;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        language = requireActivity().getSharedPreferences("Language", 0);

        v = inflater.inflate(R.layout.fragment_quotes, container, false);

        layout = LayoutInflater.from(requireContext()).inflate(R.layout.toast_view, null);
        message = layout.findViewById(R.id.message);

        errorTitle = v.findViewById(R.id.titleError);
        errorMessage = v.findViewById(R.id.messageError);

        internet = ((MainActivity) requireActivity()).isOnline(requireActivity().getApplicationContext());

        progressIndicator = v.findViewById(R.id.progress);
        progressIndicator.setVisibility(View.GONE);

        recyclerView = v.findViewById(R.id.quotesRV);
        recyclerView.setHasFixedSize(true);
        tablet = getResources().getBoolean(R.bool.isTablet);
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
            message.setText(R.string.toast_message_quotes);
            Toast toast = new Toast(requireContext().getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                Cache cache = requestQueue.getCache();
                cache.clear();
                items.clear();
                adapter.notifyDataSetChanged();
                checkInternet();
            }, 2000);
        });
        requestQueue = Volley.newRequestQueue(requireContext());

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

    @SuppressLint("SetTextI18n")
    private void checkInternet(){
        if (!internet){
            swipeRefreshLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
            errorTitle.setText(getString(R.string.no_internet_error_title));
            errorMessage.setText(getString(R.string.no_internet_error_message_quotes));
            v.findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
            parseJSON();
        }
    }

    private void parseJSON() {
        progressIndicator.setVisibility(View.VISIBLE);
        String url;

        url = "https://lijukay.github.io/Qwotable/quotes-" + language.getString("language", "en") + ".json";

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
                        adapter = new QuotesAdapter(getActivity(), items, this);
                        recyclerView.setAdapter(adapter);
                        progressIndicator.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        swipeRefreshLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        error.setVisibility(View.VISIBLE);
                        errorMessage.setText(getString(R.string.error_while_parsing_quotes));
                        errorTitle.setText(getString(R.string.error_while_parsing_title));
                        v.findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onItemClick(int position, String type) {
        String url = "https://lijukay.github.io/Qwotable/quotes-" + language.getString("language", "en") +".json";
        switch (type) {
            case "author": {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        jsonObject -> {
                            try {
                                JSONArray jsonArrayP = jsonObject.getJSONArray("Quotes");

                                JSONObject object = jsonArrayP.getJSONObject(position);

                                String authorP = object.getString("author");

                                Intent intent = new Intent(requireActivity(), Person.class);
                                intent.putExtra("author", authorP);
                                intent.putExtra("type", "author");
                                intent.putExtra("Activity", "Quotes");
                                startActivity(intent);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, Throwable::printStackTrace);
                requestQueue.add(jsonObjectRequest);

                break;
            }
            case "Found in": {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        jsonObject -> {
                            try {
                                JSONArray jsonArrayP = jsonObject.getJSONArray("Quotes");

                                JSONObject object = jsonArrayP.getJSONObject(position);

                                String authorP = object.getString("found in");

                                Intent intent = new Intent(requireActivity(), Person.class);
                                intent.putExtra("author", authorP);
                                intent.putExtra("type", "found in");
                                intent.putExtra("Activity", "Quotes");
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, Throwable::printStackTrace);
                requestQueue.add(jsonObjectRequest);

                break;
            }
            case "copy": {
                if (internet) {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            jsonObject -> {
                                try {
                                    JSONArray jsonArray = jsonObject.getJSONArray("Quotes");
                                    JSONObject object = jsonArray.getJSONObject(position);

                                    String quote = object.getString("quote");
                                    String author = object.getString("author");

                                    copyText(quote + "\n\n~ " + author);
                                } catch (JSONException e) {
                                    message.setText(R.string.error_while_parsing_toast_message_quotes);
                                    Toast toast = new Toast(requireContext().getApplicationContext());
                                    toast.setGravity(Gravity.BOTTOM, 0, 100);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);
                                    toast.show();
                                    //Toast.makeText(requireContext(), getString(R.string.error_while_parsing_toast_message_quotes), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }, Throwable::printStackTrace);
                    requestQueue.add(jsonObjectRequest);

                } else {
                    message.setText(R.string.no_internet_toast_message);
                    Toast toast = new Toast(requireContext().getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 100);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    //Toast.makeText(requireContext(), getString(R.string.no_internet_toast_message), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }



    private void copyText(String quote) {
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Quotes", quote);
        clipboard.setPrimaryClip(clip);
        message.setText(R.string.quote_copied_toast_message);
        Toast toast = new Toast(requireContext().getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
        //Toast.makeText(requireContext(), getString(R.string.quote_copied_toast_message), Toast.LENGTH_SHORT).show();
    }
}