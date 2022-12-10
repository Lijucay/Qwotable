package com.lijukay.quotesAltDesign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lijukay.quotesAltDesign.adapter.PersonAdapter;
import com.lijukay.quotesAltDesign.item.PersonItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    home home = new home();
    quotes quotes = new quotes();
    wisdom wisdom = new wisdom();
    CardView bottomnavigationview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.setting).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Settings.class)));
        bottomnavigationview = findViewById(R.id.custombnavigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer,home).commit();
        findViewById(R.id.homeImage).setBackgroundColor(getResources().getColor(R.color.selected_image_color, getTheme()));
        findViewById(R.id.quotesImage).setBackgroundColor(getResources().getColor(R.color.black, getTheme()));
        findViewById(R.id.wisdomImage).setBackgroundColor(getResources().getColor(R.color.black, getTheme()));


        findViewById(R.id.homeImage).setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer, home).commit();
            findViewById(R.id.homeImage).setBackgroundColor(getResources().getColor(R.color.selected_image_color, getTheme()));
            findViewById(R.id.quotesImage).setBackgroundColor(getResources().getColor(R.color.black, getTheme()));
            findViewById(R.id.wisdomImage).setBackgroundColor(getResources().getColor(R.color.black, getTheme()));
        });
        findViewById(R.id.wisdomImage).setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer, wisdom).commit();
            findViewById(R.id.wisdomImage).setBackgroundColor(getResources().getColor(R.color.selected_image_color, getTheme()));
            findViewById(R.id.quotesImage).setBackgroundColor(getResources().getColor(R.color.black, getTheme()));
            findViewById(R.id.homeImage).setBackgroundColor(getResources().getColor(R.color.black, getTheme()));
        });
        findViewById(R.id.quotesImage).setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer, quotes).commit();
            findViewById(R.id.quotesImage).setBackgroundColor(getResources().getColor(R.color.selected_image_color, getTheme()));
            findViewById(R.id.wisdomImage).setBackgroundColor(getResources().getColor(R.color.black, getTheme()));
            findViewById(R.id.homeImage).setBackgroundColor(getResources().getColor(R.color.black, getTheme()));
        });


    }



}