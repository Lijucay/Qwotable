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
import android.util.Log;
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
import com.lijukay.quotesAltDesign.adapter.QuotesAdapter;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.AllItem;
import com.lijukay.quotesAltDesign.service.InternetService;

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
    public static String BroadCastStringForAction = "checkInternet";
    private IntentFilter mIntentFilter;
    boolean internet;
    View v;
    private TextView errorMessage, errorTitle;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //getting the Preference where the value of language is saved in//
        language = requireActivity().getSharedPreferences("Language", 0);

        //Inflating the layout//
        v = inflater.inflate(R.layout.fragment_quotes, container, false);

        errorTitle = v.findViewById(R.id.titleError);
        errorMessage = v.findViewById(R.id.messageError);

        Intent serviceIntent = new Intent(requireContext(), InternetService.class);
        requireContext().startService(serviceIntent);

        mIntentFilter = new IntentFilter();
        //------Action of this IntentFilter: Checking the internet------//
        mIntentFilter.addAction(BroadCastStringForAction);
        internet = isOnline(requireActivity().getApplicationContext());


        //finding the Recyclerview in the inflated layout//
        recyclerView = v.findViewById(R.id.quotesRV);
        //allow the RecyclerView to avoid invalidating the whole layout when its adapter contents change//
        recyclerView.setHasFixedSize(true);
        //Checking, if the used device is a tablet (or a big device)
        boolean tablet = getResources().getBoolean(R.bool.isTablet);
        if (tablet){
            //If the device is a tablet, instead of using a LinearLayout, use a StaggeredGridLayout, so the layout does not look ugly//
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        } else {
            //If the device is not a tablet, use a LinearLayout as the device is to small to show two spans of content//
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        }
        //Finding the "error"-layout, that means the layout which is visible when there aren't any contents to show//
        error = v.findViewById(R.id.error);
        //As it is not necessary to be visible when the app is starting, the layout's visibility is set to "gone"//
        error.setVisibility(View.GONE);

        //Creating a new Items-ArrayList//
        items = new ArrayList<>();

        //Finding the SwipeRefreshLayout//
        swipeRefreshLayout = v.findViewById(R.id.quotesSRL);
        //Set the visibility to visible (this is because later in the code, the visibility can be set to "gone"
        //without this line of code, the SwipeRefreshLayout will stay invisible//
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        //Creating the onRefreshListener, which handles what should happen when the layout is refreshed//
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //Create a Toast that shows the information text ("Updating, please wait...")//
            Toast.makeText(requireActivity(),getString(R.string.refresh_message), Toast.LENGTH_SHORT).show();
            //Creating a new handler//
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                //Getting the requestQueue cache to delete it//
                Cache cache = requestQueue.getCache();
                //Clearing the cache
                //Earlier versions without that line often cached the items, so the items in the view weren't updating//
                cache.clear();
                //Clearing the ArrayList//
                items.clear();
                //Notify the adapter that the data changed (It causes a NullPointException if not used and the refresh is triggered)//
                adapter.notifyDataSetChanged();
                //check for internet//
                checkInternet();
            }, 2000);
        });
        //Creating a new request queue//
        requestQueue = Volley.newRequestQueue(requireContext());

        //check the internet//
        checkInternet();
        return v;
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
            errorMessage.setText("Connect to the internet to see quotes.");
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

    private void parseJSON() {
        //Creating a local String called url//
        String url;

        //checking the value of the language-preference, which, if not changed in the settings, is the system's language//
        if (language.getString("language", Locale.getDefault().getLanguage()).equals("de")){
            //If the language, either system's language, of if already changed, the app language is set to "de" (German)
            //The app will parse the quotes from the German JSON-File on this link//
            //TODO: Try if url = "https://lijukay.github.io/Qwotable/quotes-" +Locale.getDefault().getLanguage()+ ".json" works
            url = "https://lijukay.github.io/Qwotable/quotes-de.json";
        } else if (language.getString("language", Locale.getDefault().getLanguage()).equals("fr")){
            //If the language, either system's language, of if already changed, the app language is set to "fr" (French)
            //The app will parse the quotes from the English JSON-File on this link ('cause there is no French version of this file yet)//
            url = "https://lijukay.github.io/Qwotable/quotes-en.json";
        } else {
            //If the language is none of the above, it will parse the English-File//
            url = "https://lijukay.github.io/Qwotable/quotes-en.json";
        }

        //Creating a new JSON-Object request//
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                jsonObject -> {
                    try {
                        //Getting the JSON-Array of the JSON-Object "Quotes"//
                        JSONArray jsonArray = jsonObject.getJSONArray("Quotes");

                        //for the length of this array...//
                        for (int i = 0; i < jsonArray.length(); i++) {
                            //...the new object is the object of the Array at i//
                            JSONObject object = jsonArray.getJSONObject(i);

                            //from the object, the quote, author, foundIn gets parsed//
                            String quote = object.getString("quote");
                            String author = object.getString("author");
                            String foundIn = object.getString("found in");

                            //These things are added to the item-array//
                            items.add(new AllItem(author, quote, foundIn));
                        }
                        //creating a new Adapter which gets the activity, the items//
                        adapter = new QuotesAdapter(getActivity(), items, this);
                        //set the RecyclerView's adapter to the created adapter//
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        //If there is an issue with the file, an error layout will be shown
                        swipeRefreshLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        error.setVisibility(View.VISIBLE);
                        errorMessage.setText("There was an issue while getting your Qwotable. Please have a look at the information's page. If I already know about the issue, there will be an information there, if not, feel free to contact me.\nI will do my best to make it all work again as soon as possible.");
                        //TODO: String
                        errorTitle.setText("Oh no!");
                        //TODO:String
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

    @SuppressLint("SetTextI18n")
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