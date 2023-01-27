package com.lijukay.quotesAltDesign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.AllItem;

import java.util.ArrayList;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.AllViewHolder> {

    private final Context mContextAll;
    private final ArrayList<AllItem> mAllItem;
    private final RecyclerViewInterface recyclerViewInterface;

    public QuotesAdapter(Context contextAll, ArrayList<AllItem> allList, RecyclerViewInterface recyclerViewInterface){
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
        String foundIn = currentItemAll.getFoundIn();
        if (foundIn.equals("")){
            holderAll.mFoundIn.setVisibility(View.GONE);
        }

        holderAll.mQuoteAll.setText(allQuote);
        holderAll.mAuthorAll.setText(allAuthor);
        holderAll.mFoundIn.setText(foundIn);
    }

    @Override
    public int getItemCount() {
        return mAllItem.size();
    }

    public static class AllViewHolder extends RecyclerView.ViewHolder{
        public final TextView mQuoteAll;
        public final TextView mAuthorAll;
        public final TextView mFoundIn;
        public final Button copy;
        public final Button share;
        //public final Button saveAsImage;


        public AllViewHolder(@NonNull View itemViewAll, RecyclerViewInterface recyclerViewInterface) {
            super(itemViewAll);
            mQuoteAll = itemViewAll.findViewById(R.id.quote);
            mAuthorAll = itemViewAll.findViewById(R.id.author);
            mFoundIn = itemViewAll.findViewById(R.id.found_in);
            copy = itemViewAll.findViewById(R.id.copy);
            share = itemViewAll.findViewById(R.id.share);
            //saveAsImage = itemViewAll.findViewById(R.id.saveAsImage);

            /*saveAsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null){
                        int position = getAdapterPosition();
                        String type = "save";

                        if(position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position, type);
                        }
                    }
                }
            });*/

            mAuthorAll.setOnClickListener(view -> {
                if (recyclerViewInterface != null){
                    int position = getAdapterPosition();
                    String type = "author";

                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position, type);
                    }
                }
            });

            mFoundIn.setOnClickListener(view -> {
                if (recyclerViewInterface != null){
                    int position = getAdapterPosition();
                    String type = "Found in";

                    if (position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position, type);
                    }
                }
            });

            copy.setOnClickListener(v -> {
                if(recyclerViewInterface != null){
                    int position = getAdapterPosition();
                    String type = "copy";

                    if (position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position, type);
                    }
                }
            });

            share.setOnClickListener(v -> {
                if(recyclerViewInterface != null){
                    int position = getAdapterPosition();
                    String type = "share";

                    if (position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position, type);
                    }
                }
            });


        }
    }

}
