package com.lijukay.quotesAltDesign.fragments;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.lijukay.quotesAltDesign.Database.FavoriteDatabaseHelper;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.activities.MainActivity;
import com.lijukay.quotesAltDesign.activities.Person;
import com.lijukay.quotesAltDesign.adapter.QuotesAdapter;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.QuoteItem;
import com.lijukay.quotesAltDesign.util.InternetConnection;
import com.lijukay.quotesAltDesign.util.QwotableQuotes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class quotes extends Fragment implements RecyclerViewInterface, QwotableQuotes.QuotesFetchListener {

    private RecyclerView recyclerView;
    private QuotesAdapter adapter;
    private ArrayList<QuoteItem> items;
    private RequestQueue requestQueue;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout error;
    private SharedPreferences language;
    private View v, layout;
    private TextView errorMessage, errorTitle, message;
    private InternetConnection connection;

    @SuppressLint({"NotifyDataSetChanged", "InflateParams"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        language = requireActivity().getSharedPreferences("Language", 0);

        v = inflater.inflate(R.layout.fragment_quotes, container, false);

        connection = new InternetConnection(requireContext());

        layout = LayoutInflater.from(requireContext()).inflate(R.layout.toast_view, null);
        message = layout.findViewById(R.id.message);

        errorTitle = v.findViewById(R.id.titleError);
        errorMessage = v.findViewById(R.id.messageError);

        LinearProgressIndicator progressIndicator = v.findViewById(R.id.progress);
        progressIndicator.setVisibility(View.GONE);

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
                checkInternet();
            }, 2000);
        });
        requestQueue = Volley.newRequestQueue(requireContext());

        items = new ArrayList<>();

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
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.bottomMargin = insets.bottom;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        recyclerView.setAdapter(adapter);

        return v;
    }

    private void checkInternet() {
        if (connection.isConnected()) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);

            // Call the fetchQuotes() method in the QwotableQuotes class
            QwotableQuotes qwotableQuotes = new QwotableQuotes();
            qwotableQuotes.fetchQuotes(language.getString("language", "en"), requestQueue, this);
        } else {
            // Handle no internet connection case
            swipeRefreshLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
            errorTitle.setText(getString(R.string.no_internet_error_title));
            errorMessage.setText(getString(R.string.no_internet_error_message_quotes));
            v.findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
        }
    }

    // Implement the onQuotesFetched() method from the QuotesFetchListener interface
    @Override
    public void onQuotesFetched(ArrayList<QuoteItem> quoteItems) {
        // Update the items list and create the adapter
        items.clear();
        items.addAll(quoteItems);
        adapter = new QuotesAdapter(getActivity(), items, this);
        recyclerView.setAdapter(adapter);
    }

    // Implement the onFetchError() method from the QuotesFetchListener interface
    @Override
    public void onFetchError() {
        // Handle error case
        swipeRefreshLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        errorMessage.setText(getString(R.string.error_while_parsing_quotes));
        errorTitle.setText(getString(R.string.error_while_parsing_title));
        v.findViewById(R.id.retry).setOnClickListener(v -> checkInternet());
    }

    @Override
    public void onItemClick(int position, String type, MaterialButton mbid) {
        switch (type) {
            case "author": {
                if (items != null && !items.isEmpty()) {
                    Intent intent = new Intent(requireContext(), Person.class);
                    intent.putExtra("author", items.get(position).getAuthor());
                    intent.putExtra("type", "author");
                    intent.putExtra("Activity", "Quotes");
                }
                break;
            } case "Found in": {
                if (items != null && !items.isEmpty()) {
                    Intent intent = new Intent(requireContext(), Person.class);
                    intent.putExtra("author", items.get(position).getSource());
                    intent.putExtra("type", "found in");
                    intent.putExtra("Activity", "Quotes");
                    startActivity(intent);                }
                break;
            } case "copy": {
                if (items != null && !items.isEmpty()) {
                    copyText(items.get(position).getQuote() + "\n\n~ " + items.get(position).getAuthor());
                }
                break;
            } case "favorite": {
                if (items != null && !items.isEmpty()) {
                    String quote = items.get(position).getQuote();
                    try (FavoriteDatabaseHelper fdb = new FavoriteDatabaseHelper(requireContext())) {
                        if (!fdb.isInDB(quote)) {
                            fdb.addQwotable(quote, items.get(position).getAuthor(), items.get(position).getSource());
                            mbid.setIconResource(R.drawable.favorite_yes);
                        } else {
                            fdb.deleteOneRow(quote);
                            mbid.setIconResource(R.drawable.favorite_no);
                        }
                    }
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