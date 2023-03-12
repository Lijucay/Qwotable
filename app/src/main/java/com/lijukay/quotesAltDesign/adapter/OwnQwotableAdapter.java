package com.lijukay.quotesAltDesign.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import java.util.ArrayList;
import java.util.Calendar;

public class OwnQwotableAdapter extends RecyclerView.Adapter<OwnQwotableAdapter.OwnViewHolder> {

    private final Context context;
    private final ArrayList<String> id;
    private final ArrayList<String> qwotable;
    private final ArrayList<String> author;
    private final ArrayList<String> source;
    Activity activity;
    int position;

    RecyclerViewInterface recyclerViewInterface;

    public OwnQwotableAdapter(Activity activity, Context context, ArrayList<String> id, ArrayList<String> qwotable, ArrayList<String> author, ArrayList<String> source, RecyclerViewInterface recyclerViewInterface) {
        this.activity = activity;
        this.context = context;
        this.id = id;
        this.qwotable = qwotable;
        this.author = author;
        this.source = source;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public OwnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_quotes, parent, false);
        return new OwnViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnViewHolder holder, @SuppressLint("RecyclerView") int position){
        this.position = position;

        holder.qwotable.setText(String.valueOf(qwotable.get(position)));
        holder.author.setText(String.valueOf(author.get(position)));
        holder.source.setText(String.valueOf(source.get(position)));

        if (source.get(position).equals("")){
            holder.source.setVisibility(View.GONE);
        } else {
            holder.source.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount(){
        return id.size();
    }

    public static class OwnViewHolder extends RecyclerView.ViewHolder{

        TextView qwotable, author, source;
        MaterialCardView root_card;
        public final LinearLayout buttonLayout;
        public final Button copy, share;

        public OwnViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface){
            super(itemView);

            qwotable = itemView.findViewById(R.id.quote);
            author = itemView.findViewById(R.id.author);
            source = itemView.findViewById(R.id.found_in);
            root_card = itemView.findViewById(R.id.cardQuoteHolder);
            copy = itemView.findViewById(R.id.copy);
            share = itemView.findViewById(R.id.share);
            buttonLayout = itemView.findViewById(R.id.buttonLayout);



            root_card.setOnClickListener(view -> {
                if (recyclerViewInterface != null){
                    int position = getAdapterPosition();
                    String type = "updateOrDelete";

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

                    shareM(root_card, root_card.getContext(), buttonLayout);

                }
            });

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
}
