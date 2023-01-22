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

    private final Context mContext;
    private final ArrayList<wisdomItem> mItem;
    private final RecyclerViewInterface recyclerViewInterface;

    public wisdomAdapter (Context context, ArrayList<wisdomItem> item, RecyclerViewInterface recyclerViewInterface){
        mContext = context;
        mItem = item;
        this.recyclerViewInterface = recyclerViewInterface;

    }

    @NonNull
    @Override
    public wisdomAdapter.wisdomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_wisdom, parent, false);
        return new wisdomAdapter.wisdomViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull wisdomAdapter.wisdomViewHolder holder, int position) {
        wisdomItem currentItem = mItem.get(position);

        String wisdom = currentItem.getWisdom();
        String author = currentItem.getAuthor();
        String foundIn = currentItem.getFoundIn();
        String title = currentItem.getTitle();

        holder.mWisdom.setText(wisdom);
        holder.mAuthor.setText(author);
        holder.mFoundIn.setText(foundIn);
        holder.mTitle.setText(title);

    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    public static class wisdomViewHolder extends RecyclerView.ViewHolder{
        public final TextView mTitle;
        public final TextView mWisdom;
        public final TextView mFoundIn;
        public final TextView mAuthor;


        public wisdomViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.wisdomTitle);
            mWisdom = itemView.findViewById(R.id.wisdom);
            mFoundIn = itemView.findViewById(R.id.found_in_wisdomc);
            mAuthor = itemView.findViewById(R.id.author);

            mAuthor.setOnClickListener(view -> {
                if (recyclerViewInterface != null){
                    int position = getAdapterPosition();
                    String type = "author";

                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position, type);
                    }
                }
            });

            mFoundIn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                String type = "found in";

                if (position != RecyclerView.NO_POSITION){
                    recyclerViewInterface.onItemClick(position, type);
                }
            });

            mWisdom.setOnClickListener(v -> {
                int position = getAdapterPosition();
                String type = "wisdom";

                if (position != RecyclerView.NO_POSITION){
                    recyclerViewInterface.onItemClick(position, type);
                }
            });
        }
    }
}
