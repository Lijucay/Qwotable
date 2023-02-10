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
import com.lijukay.quotesAltDesign.item.AllItem;

import java.util.ArrayList;
import java.util.Calendar;

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
        public final MaterialCardView layout;
        public final LinearLayout buttonLayout;
        //public final Button saveAsImage;


        public AllViewHolder(@NonNull View itemViewAll, RecyclerViewInterface recyclerViewInterface) {
            super(itemViewAll);
            mQuoteAll = itemViewAll.findViewById(R.id.quote);
            mAuthorAll = itemViewAll.findViewById(R.id.author);
            mFoundIn = itemViewAll.findViewById(R.id.found_in);
            copy = itemViewAll.findViewById(R.id.copy);
            share = itemViewAll.findViewById(R.id.share);
            layout = itemViewAll.findViewById(R.id.cardQuoteHolder);
            buttonLayout = itemViewAll.findViewById(R.id.buttonLayout);

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

                    shareM(layout, layout.getContext(), buttonLayout);

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


