package com.lijukay.quotesAltDesign.fragments;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.activities.Person;
import com.lijukay.quotesAltDesign.adapter.QuotesAdapter;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.AllItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    View v;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

                        adapter = new QuotesAdapter(getActivity(), items, this);
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
        String url;
        switch (type) {
            case "author": {
                if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")) {
                    url = "https://lijukay.github.io/Qwotable/quotes-de.json";
                } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")) {
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

                break;
            }
            case "Found in": {
                if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")) {
                    url = "https://lijukay.github.io/Qwotable/quotes-de.json";
                } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")) {
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

                break;
            }
            case "Quote": {
                if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")) {
                    url = "https://lijukay.github.io/Qwotable/quotes-de.json";
                } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")) {
                    url = "https://lijukay.github.io/Qwotable/quotes-en.json";
                } else {
                    url = "https://lijukay.github.io/Qwotable/quotes-en.json";
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        jsonObject -> {
                            try {
                                JSONArray jsonArray = jsonObject.getJSONArray("Quotes");
                                JSONObject object = jsonArray.getJSONObject(position);

                                String quote = object.getString("quote");
                                String author = object.getString("author");
                                String foundIn = object.getString("found in");

                                showDialogs(quote, author, foundIn);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, Throwable::printStackTrace);
                requestQueue.add(jsonObjectRequest);
                break;
            }
        }
    }

    private void showDialogs(String quote, String author, String foundIn) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_bg);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.lottie_file);
        lottieAnimationView.setVisibility(View.GONE);


        TextView messageText = dialog.findViewById(R.id.message_text);
        messageText.setVisibility(View.VISIBLE);
        messageText.setText(quote + "\n\n" + author + "\n\n" + foundIn);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) messageText.getLayoutParams();
        layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        messageText.setLayoutParams(layoutParams);

        TextView titleText = dialog.findViewById(R.id.custom_title);
        titleText.setVisibility(View.VISIBLE);
        titleText.setText("More Options - Quotes");

        Button copy = dialog.findViewById(R.id.positive_button);
        copy.setText("Copy");

        Button share = dialog.findViewById(R.id.negative_button);
        share.setText("Share");

        copy.setOnClickListener(view -> copyText(quote + "\n\n~ " + author));

        share.setOnClickListener(view -> {
            Intent shareText = new Intent();
            shareText.setAction(Intent.ACTION_SEND);
            shareText.putExtra(Intent.EXTRA_TEXT, quote + "\n\n~" + author);
            shareText.setType("text/plain");
            Intent sendText = Intent.createChooser(shareText, null);
            startActivity(sendText);
            //todo: create class for sharing Qwotables in a picture with a QR-Code
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void copyText(String quote) {
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Quotes", quote);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), "Qwotable was copied", Toast.LENGTH_SHORT).show();
    }
}