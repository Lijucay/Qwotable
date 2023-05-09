package com.lijukay.quotesAltDesign.fragments;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.adapter.QuotesAdapter;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.QuoteItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class dwyl_quotes extends Fragment implements RecyclerViewInterface {

    private View layout;
    private RecyclerView recyclerView;
    private TextView message;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v1 = inflater.inflate(R.layout.fragment_dwyl_quotes, container, false);

        layout = LayoutInflater.from(requireContext()).inflate(R.layout.toast_view, null);
        message = layout.findViewById(R.id.message);

        recyclerView = v1.findViewById(R.id.quotesRV);
        recyclerView.setHasFixedSize(true);
        boolean tablet = getResources().getBoolean(R.bool.isTablet);
        if (tablet){
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        }

        ArrayList<QuoteItem> items = new ArrayList<>();

        try {

            InputStream inputStream = getResources().openRawResource(R.raw.quotes);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String jsonArrayStr = builder.toString();
            JSONArray jsonArray = new JSONArray(jsonArrayStr);

            // Iterate over the JSONArray
            for (int i = 0; i < jsonArray.length(); i++) {
                // Get the JSONObject at the current index
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Get the author and text values from the JSONObject
                String author = jsonObject.getString("author");
                String quote = jsonObject.getString("text");

                items.add(new QuoteItem(author, quote, ""));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        QuotesAdapter adapter = new QuotesAdapter(getActivity(), items, this);
        recyclerView.setAdapter(adapter);

        if (!tablet) ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            recyclerView.setPadding(0,0,0,insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        }); else ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            recyclerView.setPadding(0,insets.top,0,insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        return v1;
    }

    @Override
    public void onItemClick(int position, String type, MaterialButton materialButton) {
        if ("copy".equals(type)) {
            try {
                InputStream inputStream = getResources().openRawResource(R.raw.quotes);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                String jsonArrayStr = builder.toString();
                JSONArray jsonArray = new JSONArray(jsonArrayStr);

                JSONObject jsonObject = jsonArray.getJSONObject(position);

                // Get the author and text values from the JSONObject
                String author = jsonObject.getString("author");
                String quote = jsonObject.getString("text");

                copyText(quote + "\n\n~ " + author);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
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