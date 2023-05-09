package com.lijukay.quotesAltDesign.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.LicenseItem;

import java.util.ArrayList;
import java.util.Calendar;

public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.ViewHolder> {

    private final Context CONTEXT;
    private final ArrayList<LicenseItem> ITEMS;
    private final RecyclerViewInterface RECYCLERVIEW_INTERFACE;

    public LicenseAdapter(Context context, ArrayList<LicenseItem> itemsList, RecyclerViewInterface recyclerViewInterface){
        CONTEXT = context;
        ITEMS = itemsList;
        RECYCLERVIEW_INTERFACE = recyclerViewInterface;
    }

    @NonNull
    @Override
    public LicenseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(CONTEXT).inflate(R.layout.license_card, parent, false);
        return new LicenseAdapter.ViewHolder(v, RECYCLERVIEW_INTERFACE);
    }

    @Override
    public void onBindViewHolder(@NonNull LicenseAdapter.ViewHolder holder, int position) {
        LicenseItem currentItem = ITEMS.get(position);

        String licenseTitle = currentItem.getLicenseTitle();
        String license = currentItem.getLicense();

        holder.LICENSE_TITLE.setText(licenseTitle);
        holder.LICENSE.setText(license);

    }

    @Override
    public int getItemCount() {
        return ITEMS.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView LICENSE_TITLE;
        private final TextView LICENSE;


        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            LICENSE_TITLE = itemView.findViewById(R.id.licenseTitle);
            LICENSE = itemView.findViewById(R.id.license);
            CardView LICENSE_CARD_HOLDER = itemView.findViewById(R.id.licenseCard);

            LICENSE_CARD_HOLDER.setOnClickListener(view -> {
                if (recyclerViewInterface != null){
                    int position = getAdapterPosition();
                    String type = "license";

                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position, type, null);
                    }
                }
            });
        }

    }
}
