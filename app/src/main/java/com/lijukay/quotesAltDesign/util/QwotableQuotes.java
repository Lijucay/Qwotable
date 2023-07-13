package com.lijukay.quotesAltDesign.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lijukay.quotesAltDesign.item.QuoteItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QwotableQuotes {

    /*public ArrayList<QuoteItem> getQuoteItems(String language, RequestQueue requestQueue) {

        String url = "https://lijukay.github.io/Qwotable/quotes-" + language + ".json";
        ArrayList<QuoteItem> items = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                jsonObject -> {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("Quotes");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String quote = object.getString("quote");
                            String author = object.getString("author");
                            String foundIn = object.getString("found in");

                            items.add(new QuoteItem(author, quote, foundIn));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        requestQueue.add(jsonObjectRequest);

        return items;
    }*/

    public void fetchQuotes(String language, RequestQueue requestQueue, final QuotesFetchListener listener) {
        String url = "https://lijukay.github.io/Qwotable/quotes-" + language + ".json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                jsonObject -> {
                    ArrayList<QuoteItem> quoteItems = new ArrayList<>();
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("Quotes");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String quote = object.getString("quote");
                            String author = object.getString("author");
                            String foundIn = object.getString("found in");

                            quoteItems.add(new QuoteItem(author, quote, foundIn));
                        }

                        // Callback to notify the listener with the retrieved quote items
                        listener.onQuotesFetched(quoteItems);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Callback to notify the listener about the error
                        listener.onFetchError();
                    }
                }, error -> {
            error.printStackTrace();
            // Callback to notify the listener about the error
            listener.onFetchError();
        });

        requestQueue.add(jsonObjectRequest);
    }

    public interface QuotesFetchListener {
        void onQuotesFetched(ArrayList<QuoteItem> quoteItems);
        void onFetchError();
    }

}
