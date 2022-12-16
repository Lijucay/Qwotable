package com.lijukay.quotesAltDesign.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.fragments.home;
import com.lijukay.quotesAltDesign.fragments.quotes;
import com.lijukay.quotesAltDesign.fragments.wisdom;

public class MainActivity extends AppCompatActivity {
    com.lijukay.quotesAltDesign.fragments.home home = new home();
    com.lijukay.quotesAltDesign.fragments.quotes quotes = new quotes();
    com.lijukay.quotesAltDesign.fragments.wisdom wisdom = new wisdom();
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