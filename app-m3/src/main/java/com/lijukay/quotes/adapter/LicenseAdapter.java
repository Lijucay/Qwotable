package com.lijukay.quotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lijukay.quotes.R;
import com.lijukay.quotes.interfaces.RecyclerViewInterface;
import com.lijukay.quotes.item.LicenseItem;

import java.util.ArrayList;

public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<LicenseItem> items;
    private final RecyclerViewInterface recyclerViewInterface;

    public LicenseAdapter(Context context, ArrayList<LicenseItem> itemsList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        items = itemsList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public LicenseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.license_card, parent, false);
        return new LicenseAdapter.ViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull LicenseAdapter.ViewHolder holder, int position) {
        LicenseItem currentItem = items.get(position);

        String licenseTitle = currentItem.getLicenseTitle();
        String license = currentItem.getLicense();

        holder.LICENSE_TITLE.setText(licenseTitle);
        holder.LICENSE.setText(license);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView LICENSE_TITLE;
        private final TextView LICENSE;


        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            LICENSE_TITLE = itemView.findViewById(R.id.licenseTitle);
            LICENSE = itemView.findViewById(R.id.license);
            CardView LICENSE_CARD_HOLDER = itemView.findViewById(R.id.licenseCard);

            LICENSE_CARD_HOLDER.setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();
                    String type = "license";

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position, type, null);
                    }
                }
            });
        }

    }
}
