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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.lijukay.quotesAltDesign.Database.FavoriteDatabaseHelper;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.QuoteItem;

import java.util.ArrayList;
import java.util.Calendar;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.ViewHolder> {

    private final Context CONTEXT;
    private final ArrayList<QuoteItem> ITEMS;
    private final RecyclerViewInterface RECYCLERVIEW_INTERFACE;

    public QuotesAdapter(Context context, ArrayList<QuoteItem> items_list, RecyclerViewInterface recyclerViewInterface){
        CONTEXT = context;
        ITEMS = items_list;
        RECYCLERVIEW_INTERFACE = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(CONTEXT).inflate(R.layout.card_quotes, parent, false);
        return new ViewHolder(v, RECYCLERVIEW_INTERFACE);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuoteItem currentItem = ITEMS.get(position);

        String quote = currentItem.getQuote();
        String author = currentItem.getAuthor();
        String foundIn = currentItem.getSource();



        if (foundIn.equals("")){
            holder.SOURCE.setVisibility(View.GONE);
        }

        try (FavoriteDatabaseHelper fdb = new FavoriteDatabaseHelper(CONTEXT)) {
            MaterialButton fb = holder.FAVORITE;
            if (fdb.isInDB(quote)) {
                fb.setIconResource(R.drawable.favorite_yes);
            } else {
                fb.setIconResource(R.drawable.favorite_no);
            }
        }

        holder.QUOTE.setText(quote);
        holder.AUTHOR.setText(author);
        holder.SOURCE.setText(foundIn);

    }

    @Override
    public int getItemCount() {
        return ITEMS.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView QUOTE;
        private final TextView AUTHOR;
        private final TextView SOURCE;
        private final MaterialButton FAVORITE;
        private final MaterialCardView LAYOUT;
        private final LinearLayout BUTTON_LAYOUT;


        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            QUOTE = itemView.findViewById(R.id.quote);
            AUTHOR = itemView.findViewById(R.id.author);
            SOURCE = itemView.findViewById(R.id.found_in);
            FAVORITE = itemView.findViewById(R.id.favorite);
            LAYOUT = itemView.findViewById(R.id.cardQuoteHolder);
            BUTTON_LAYOUT = itemView.findViewById(R.id.buttonLayout);
            MaterialButton copy = itemView.findViewById(R.id.copy);
            MaterialButton share = itemView.findViewById(R.id.share);

            AUTHOR.setOnClickListener(view -> {
                if (recyclerViewInterface != null){
                    int position = getAdapterPosition();
                    String type = "author";

                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position, type, null);
                    }
                }
            });

            SOURCE.setOnClickListener(view -> {
                if (recyclerViewInterface != null){
                    int position = getAdapterPosition();
                    String type = "Found in";

                    if (position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position, type, null);
                    }
                }
            });

            copy.setOnClickListener(v -> {
                if(recyclerViewInterface != null){
                    int position = getAdapterPosition();
                    String type = "copy";

                    if (position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position, type, null);
                    }
                }
            });

            FAVORITE.setOnClickListener(v -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();
                    String type = "favorite";

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position, type, FAVORITE);
                    }
                }

            });

            share.setOnClickListener(v -> {
                if(recyclerViewInterface != null){

                    shareM(LAYOUT, LAYOUT.getContext(), BUTTON_LAYOUT);

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
        if (bgDrawable!=null) {
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
                "qwotable at " + hour + "_"+ minute + "_" + second,
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


