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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.WisdomItem;

import java.util.ArrayList;
import java.util.Calendar;

public class WisdomAdapter extends RecyclerView.Adapter<WisdomAdapter.wisdomViewHolder> {

    private final Context context;
    private final ArrayList<WisdomItem> items;
    private final RecyclerViewInterface recyclerViewInterface;

    public WisdomAdapter(Context context, ArrayList<WisdomItem> item, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        items = item;
        this.recyclerViewInterface = recyclerViewInterface;

    }

    @NonNull
    @Override
    public WisdomAdapter.wisdomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_wisdom, parent, false);
        return new WisdomAdapter.wisdomViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull WisdomAdapter.wisdomViewHolder holder, int position) {
        WisdomItem currentItem = items.get(position);

        String wisdom = currentItem.getWisdom();
        String author = currentItem.getAuthor();
        String foundIn = currentItem.getSource();
        String title = currentItem.getTitle();

        holder.mWisdom.setText(wisdom);
        holder.mAuthor.setText(author);
        holder.mFoundIn.setText(foundIn);
        holder.mTitle.setText(title);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class wisdomViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitle;
        private final TextView mWisdom;
        private final TextView mFoundIn;
        private final TextView mAuthor;
        private final MaterialCardView layout;
        private final LinearLayout buttonLayout;


        public wisdomViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.wisdomTitle);
            mWisdom = itemView.findViewById(R.id.wisdom);
            mFoundIn = itemView.findViewById(R.id.found_in_wisdomc);
            mAuthor = itemView.findViewById(R.id.author);
            Button copy = itemView.findViewById(R.id.copy);
            Button share = itemView.findViewById(R.id.share);
            layout = itemView.findViewById(R.id.cardWisdomHolder);
            buttonLayout = itemView.findViewById(R.id.buttonLayout);

            copy.setOnClickListener(v -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();
                    String type = "copy";

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position, type, null);
                    }
                }
            });

            share.setOnClickListener(v -> {
                if (recyclerViewInterface != null) {

                    shareM(layout, layout.getContext(), buttonLayout);

                }
            });


            mAuthor.setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();
                    String type = "author";

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position, type, null);
                    }
                }
            });

            mFoundIn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                String type = "found in";

                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(position, type, null);
                }
            });

        }
    }

    public static void shareM(ViewGroup view, Context context, LinearLayout buttonLayout) {

        Calendar calendar = Calendar.getInstance(); //Get instance to create unique names for the pictures

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        buttonLayout.setVisibility(View.GONE);

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        String bitmapUri = MediaStore.Images.Media.insertImage(
                context.getContentResolver(),
                bitmap,
                "qwotable at " + hour + "_" + minute + "_" + second,
                "Qwotable made this"
        );
        Uri uri = Uri.parse(bitmapUri);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpg");

        try {
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        } catch (Exception e) {
            Toast.makeText(
                    context,
                    "Unable to share quote.",
                    Toast.LENGTH_SHORT
            ).show();
        }
        context.startActivity(Intent.createChooser(intent, "Share this via "));
        view.setBackground(bgDrawable);
        bitmap.recycle();
        buttonLayout.setVisibility(View.VISIBLE);
    }

}
