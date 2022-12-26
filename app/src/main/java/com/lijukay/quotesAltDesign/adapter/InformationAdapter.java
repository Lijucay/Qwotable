package com.lijukay.quotesAltDesign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.item.InformationItem;

import java.time.temporal.Temporal;
import java.util.ArrayList;

public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.InfoViewHolder> {
    private final Context mContext;
    private final ArrayList<InformationItem> informationItems;

    public InformationAdapter (Context context, ArrayList<InformationItem> informationItems){
        mContext = context;
        this.informationItems = informationItems;
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.information_item, parent, false);
        return new InfoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        InformationItem currentItem = informationItems.get(position);

        String title = currentItem.getTitle();
        String message = currentItem.getMessage();
        String date = currentItem.getDate();

        holder.mTitle.setText(title);
        holder.mMessage.setText(message);
        holder.mDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return informationItems.size();
    }

    public static class InfoViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitle;
        private final TextView mMessage;
        private final TextView mDate;

        public InfoViewHolder(@NonNull View itemView){
            super(itemView);
            mTitle = itemView.findViewById(R.id.titleInfo);
            mMessage = itemView.findViewById(R.id.messageInfo);
            mDate = itemView.findViewById(R.id.dateInfo);


        }

    }
}
