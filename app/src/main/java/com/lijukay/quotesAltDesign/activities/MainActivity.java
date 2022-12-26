package com.lijukay.quotesAltDesign.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.fragments.home;
import com.lijukay.quotesAltDesign.fragments.quotes;
import com.lijukay.quotesAltDesign.fragments.wisdom;

import org.w3c.dom.Text;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    com.lijukay.quotesAltDesign.fragments.home home = new home();
    com.lijukay.quotesAltDesign.fragments.quotes quotes = new quotes();
    com.lijukay.quotesAltDesign.fragments.wisdom wisdom = new wisdom();
    Fragment fragment;
    SharedPreferences language;
    String lang;

    CardView bottomnavigationview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        language = getSharedPreferences("Language", 0);
        lang = language.getString("language", Locale.getDefault().getLanguage());
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        setContentView(R.layout.activity_main);

        findViewById(R.id.setting).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Settings.class)));
        bottomnavigationview = findViewById(R.id.custombnavigation);



        TextView title = findViewById(R.id.custom_title_main);
        title.setText(getString(R.string.app_name));

        getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer,home).commit();

        findViewById(R.id.homeImage).setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer, home).commit();
            title.setText(getString(R.string.app_name));
        });

        findViewById(R.id.wisdomImage).setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer, wisdom).commit();
            title.setText("Wisdom");
        });

        findViewById(R.id.quotesImage).setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer, quotes).commit();
            title.setText("Quotes");
        });

    }

    @Override
    public void onBackPressed(){
        this.finishAffinity();
    }
}