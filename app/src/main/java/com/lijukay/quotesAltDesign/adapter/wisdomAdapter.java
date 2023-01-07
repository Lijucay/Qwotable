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
import com.lijukay.quotesAltDesign.item.wisdomItem;

import java.util.ArrayList;

public class wisdomAdapter extends RecyclerView.Adapter<wisdomAdapter.wisdomViewHolder> {

    private final Context mContextAll;
    private final ArrayList<wisdomItem> mAllItem;
    private final RecyclerViewInterface recyclerViewInterface;

    public wisdomAdapter (Context contextAll, ArrayList<wisdomItem> allList, RecyclerViewInterface recyclerViewInterface){
        mContextAll = contextAll;
        mAllItem = allList;
        this.recyclerViewInterface = recyclerViewInterface;

    }

    @NonNull
    @Override
    public wisdomAdapter.wisdomViewHolder onCreateViewHolder(@NonNull ViewGroup parentAll, int viewTypeAll) {
        View vA = LayoutInflater.from(mContextAll).inflate(R.layout.card_wisdom, parentAll, false);
        return new wisdomAdapter.wisdomViewHolder(vA, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull wisdomAdapter.wisdomViewHolder holderAll, int positionAll) {
        wisdomItem currentItemAll = mAllItem.get(positionAll);

        String allWisdom = currentItemAll.getWisdomAll();
        String allAuthor = currentItemAll.getAuthorAll();
        String allTitle = currentItemAll.getTitleAll();

        holderAll.mWisdomAll.setText(allWisdom);
        holderAll.mAuthorAll.setText(allAuthor);
        holderAll.mTitleWisdom.setText(allTitle);

    }

    @Override
    public int getItemCount() {
        return mAllItem.size();
    }

    public static class wisdomViewHolder extends RecyclerView.ViewHolder{
        public final TextView mTitleWisdom;
        public final TextView mWisdomAll;
        public final TextView mAuthorAll;


        public wisdomViewHolder(@NonNull View itemViewAll, RecyclerViewInterface recyclerViewInterface) {
            super(itemViewAll);
            mTitleWisdom = itemViewAll.findViewById(R.id.wisdomTitle);
            mWisdomAll = itemViewAll.findViewById(R.id.wisdom);
            mAuthorAll = itemViewAll.findViewById(R.id.author);

            mAuthorAll.setOnClickListener(view -> {
                if (recyclerViewInterface != null){
                    int position = getAdapterPosition();
                    String type = "author";

                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position, type);
                    }
                }
            });

        }
    }

}
