package com.lijukay.quotes.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.lijukay.quotes.R;
import com.lijukay.quotes.adapter.QuotesAdapter;
import com.lijukay.quotes.database.FavoriteDatabaseHelper;
import com.lijukay.quotes.interfaces.RecyclerViewInterface;
import com.lijukay.quotes.item.QuoteItem;

import java.util.ArrayList;

public class Favorites extends Fragment implements RecyclerViewInterface {

    private FavoriteDatabaseHelper fdb;
    private ArrayList<QuoteItem> items;
    private QuotesAdapter ownQwotableAdapter;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_favorites, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.favoritesRV);

        fdb = new FavoriteDatabaseHelper(requireContext());

        items = new ArrayList<>();

        storeDataInArrays();

        ownQwotableAdapter = new QuotesAdapter(requireContext(), items, this);
        recyclerView.setAdapter(ownQwotableAdapter);

        boolean tablet = getResources().getBoolean(R.bool.isTablet);
        if (tablet) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        }
        return v;
    }

    private void storeDataInArrays() {
        Cursor cursor = fdb.readAllData();
        if (cursor.getCount() == 0) {
            v.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()) {
                items.add(new QuoteItem(cursor.getString(1), cursor.getString(0), cursor.getString(2)));
            }
            v.findViewById(R.id.no_data).setVisibility(View.GONE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemClick(int position, String type, MaterialButton mbid) {
        if (type.equals("favorite")) {
            try (FavoriteDatabaseHelper fdb = new FavoriteDatabaseHelper(requireContext())) {
                fdb.deleteOneRow(items.get(position).getQuote());
                items.clear();
                storeDataInArrays();
                ownQwotableAdapter.notifyDataSetChanged();
            }
        }
    }
}