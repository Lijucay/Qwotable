package com.lijukay.quotesAltDesign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.AllItem;

import java.util.ArrayList;

public class AllAdapter extends RecyclerView.Adapter<AllAdapter.AllViewHolder> {

    private final Context mContextAll;
    private final ArrayList<AllItem> mAllItem;
    private final RecyclerViewInterface recyclerViewInterface;

    public AllAdapter (Context contextAll, ArrayList<AllItem> allList, RecyclerViewInterface recyclerViewInterface){
        mContextAll = contextAll;
        mAllItem = allList;
        this.recyclerViewInterface = recyclerViewInterface;

    }

    @NonNull
    @Override
    public AllViewHolder onCreateViewHolder(@NonNull ViewGroup parentAll, int viewTypeAll) {
        View vA = LayoutInflater.from(mContextAll).inflate(R.layout.card_quotes, parentAll, false);
        return new AllViewHolder(vA, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AllViewHolder holderAll, int positionAll) {
        AllItem currentItemAll = mAllItem.get(positionAll);

        String allQuote = currentItemAll.getQuoteAll();
        String allAuthor = currentItemAll.getAuthorAll();

        holderAll.mQuoteAll.setText(allQuote);
        holderAll.mAuthorAll.setText(allAuthor);
    }

    @Override
    public int getItemCount() {
        return mAllItem.size();
    }

    public static class AllViewHolder extends RecyclerView.ViewHolder{
        public final TextView mQuoteAll;
        public final TextView mAuthorAll;


        public AllViewHolder(@NonNull View itemViewAll, RecyclerViewInterface recyclerViewInterface) {
            super(itemViewAll);
            mQuoteAll = itemViewAll.findViewById(R.id.quoteAQ);
            mAuthorAll = itemViewAll.findViewById(R.id.authorAQ);

            mAuthorAll.setOnClickListener(view -> {
                if (recyclerViewInterface != null){
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position);
                    }
                }
            });

        }
    }

}
