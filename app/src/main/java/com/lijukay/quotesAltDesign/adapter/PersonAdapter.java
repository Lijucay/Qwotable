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
import com.lijukay.quotesAltDesign.item.PersonItem;

import java.util.ArrayList;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PQViewHolder> {
    private final Context mContextP;
    private final ArrayList<PersonItem> mPItem;
    private final RecyclerViewInterface recyclerViewInterface;

    public PersonAdapter (Context contextPQ, ArrayList<PersonItem> pList, RecyclerViewInterface recyclerViewInterface){
        mContextP = contextPQ;
        mPItem = pList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public PQViewHolder onCreateViewHolder(@NonNull ViewGroup parentPQ, int viewTypePQ) {
        View vPQ = LayoutInflater.from(mContextP).inflate(R.layout.person_item, parentPQ, false);
        return new PQViewHolder(vPQ, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull PQViewHolder holderPQ, int positionPQ) {
        PersonItem currentItemPQ = mPItem.get(positionPQ);

        String PQQuote = currentItemPQ.getQuotePQ();
        String PQAuthor = currentItemPQ.getAuthorPQ();

        holderPQ.mQuotePQ.setText(PQQuote);
        holderPQ.mAuthorPQ.setText(PQAuthor);
    }

    @Override
    public int getItemCount() {
        return mPItem.size();
    }

    public static class PQViewHolder extends RecyclerView.ViewHolder{
        public final TextView mQuotePQ;
        public final TextView mAuthorPQ;


        public PQViewHolder(@NonNull View itemViewPQ, RecyclerViewInterface recyclerViewInterface) {
            super(itemViewPQ);
            mQuotePQ = itemViewPQ.findViewById(R.id.personquote);
            mAuthorPQ = itemViewPQ.findViewById(R.id.personauthor);

            itemViewPQ.setOnClickListener(view -> {
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

