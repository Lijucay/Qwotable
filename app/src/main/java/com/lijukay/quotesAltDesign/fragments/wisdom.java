package com.lijukay.quotesAltDesign.fragments;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        language = requireActivity().getSharedPreferences("Language", 0);
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_wisdom, container, false);


        errorTitle = v.findViewById(R.id.titleError);
        errorMessage = v.findViewById(R.id.messageError);

        Intent serviceIntent = new Intent(requireContext(), InternetService.class);
        requireContext().startService(serviceIntent);

        mIntentFilter = new IntentFilter();
        //------Action of this IntentFilter: Checking the internet------//
        mIntentFilter.addAction(BroadCastStringForAction);
        internet = isOnline(requireActivity().getApplicationContext());


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
                checkInternet();
            }, 2000);
        });
        requestQueue = Volley.newRequestQueue(requireContext());

        //Finding the "error"-layout, that means the layout which is visible when there aren't any contents to show//
        error = v.findViewById(R.id.error);
        //As it is not necessary to be visible when the app is starting, the layout's visibility is set to "gone"//
        error.setVisibility(View.GONE);

        checkInternet();


        return v;


    }

    private void parseJSON() {
        String url;
        if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")){
            url = "https://lijukay.github.io/Qwotable/wisdom-de.json";
        } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")){
            url = "https://lijukay.github.io/Qwotable/wisdom-en.json";
        } else {
            url = "https://lijukay.github.io/Qwotable/wisdom-en.json";
        }

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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public void onItemClick(int position, String type) {

        switch (type) {
            case "author": {
                String url;
                if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")) {
                    url = "https://lijukay.github.io/Qwotable/wisdom-de.json";
                } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")) {
                    url = "https://lijukay.github.io/Qwotable/wisdom-en.json";
                } else {
                    url = "https://lijukay.github.io/Qwotable/wisdom-en.json";
                }

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
                String url;
                if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")) {
                    url = "https://lijukay.github.io/Qwotable/wisdom-de.json";
                } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")) {
                    url = "https://lijukay.github.io/Qwotable/wisdom-en.json";
                } else {
                    url = "https://lijukay.github.io/Qwotable/wisdom-en.json";
                }

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
            case "wisdom": {
                String url;
                if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")) {
                    url = "https://lijukay.github.io/Qwotable/wisdom-de.json";
                } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")) {
                    url = "https://lijukay.github.io/Qwotable/wisdom-en.json";
                } else {
                    url = "https://lijukay.github.io/Qwotable/wisdom-en.json";
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        jsonObject -> {
                            try {
                                JSONArray jsonArray = jsonObject.getJSONArray("Wisdom");
                                 JSONObject object = jsonArray.getJSONObject(position);
                                  String wisdom = object.getString("wisdom");
                                  String author = object.getString("author");
                                  String foundIn = object.getString("found in");
                                  String title = object.getString("title");

                                  showDialog(wisdom, author, foundIn, title);


                                  adapter = new wisdomAdapter(getActivity(), items, this);
                                  recyclerView.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, Throwable::printStackTrace);
                requestQueue.add(jsonObjectRequest);
                break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void showDialog(String wisdom, String author, String foundIn, String title) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_bg);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.lottie_file);
        lottieAnimationView.setVisibility(View.GONE);


        TextView messageText = dialog.findViewById(R.id.message_text);
        messageText.setVisibility(View.VISIBLE);
        messageText.setText(title + "\n\n" + wisdom + "\n\n" + author + "\n\n" + foundIn);

        CardView messageCard = dialog.findViewById(R.id.message_card);
        messageCard.setVisibility(View.VISIBLE);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
        layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        messageCard.setLayoutParams(layoutParams);

        TextView titleText = dialog.findViewById(R.id.custom_title);
        titleText.setVisibility(View.VISIBLE);
        titleText.setText("More Options - Quotes"); //TODO: String

        Button copy = dialog.findViewById(R.id.positive_button);
        copy.setText("Copy"); //Todo: String

        Button share = dialog.findViewById(R.id.negative_button);
        share.setText("Share"); //Todo: String

        Button cancel = dialog.findViewById(R.id.neutral_button);

        cancel.setOnClickListener(v -> dialog.dismiss());

        copy.setOnClickListener(view -> copyText(wisdom + "\n\n~ " + author));

        share.setOnClickListener(view -> {
            Intent shareText = new Intent();
            shareText.setAction(Intent.ACTION_SEND);
            shareText.putExtra(Intent.EXTRA_TEXT, wisdom + "\n\n~" + author);
            shareText.setType("text/plain");
            Intent sendText = Intent.createChooser(shareText, null);
            startActivity(sendText);
            //todo: create class for sharing Qwotables in a picture with a QR-Code
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void copyText(String wisdom) {
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Wisdom", wisdom);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), "Qwotable was copied", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void checkInternet(){
        //checking if internet is true or false//
        if (!internet){
            //If there is no internet, the recyclerview and the refreshLayout are gone, but the error view is visible//
            swipeRefreshLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
            errorTitle.setText("No internet");
            //Todo: String
            errorMessage.setText("Connect to the internet to see wisdom");
            //Todo: String
            //If there is no internet, this line checks every 2000 millis, if there still is no internet//
            v.findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
        } else {
            //If there is internet, the Visibility of the swipeRefreshLayout and the recyclerView is set to Visible and the error view disappears//
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
            //parsing the JSON as internet is available
            parseJSON();
        }
    }


    public final BroadcastReceiver InternetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadCastStringForAction)) {
                internet = intent.getStringExtra("online_status").equals("true");
            }
        }
    };


    public boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(InternetReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireContext().registerReceiver(InternetReceiver, mIntentFilter);
    }

}