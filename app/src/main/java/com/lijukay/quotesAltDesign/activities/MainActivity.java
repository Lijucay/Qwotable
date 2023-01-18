package com.lijukay.quotesAltDesign.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.adapter.MainAdapter;
import com.lijukay.quotesAltDesign.fragments.home;
import com.lijukay.quotesAltDesign.fragments.quotes;
import com.lijukay.quotesAltDesign.fragments.wisdom;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    SharedPreferences languageSharedPreference, colorSharedPreference;
    String languageString;
    BottomNavigationView bottomnavigationview;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    ViewPager2 viewPager2;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languageSharedPreference = getSharedPreferences("Language", 0);
        languageString = languageSharedPreference.getString("language", Locale.getDefault().getLanguage());
        Locale locale = new Locale(languageString);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        colorSharedPreference = getSharedPreferences("Colors", 0);

        switch (colorSharedPreference.getString("color", "red")){
            case "red":
                setTheme(R.style.AppTheme);
                break;
            case "pink":
                setTheme(R.style.AppThemePink);
                break;
            case "green":
                setTheme(R.style.AppThemeGreen);
                break;
        }



        setContentView(R.layout.activity_main);

        findViewById(R.id.setting).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Settings.class)));
        bottomnavigationview = findViewById(R.id.bottomNavigation);
        viewPager2 = findViewById(R.id.viewPager);

        TextView title = findViewById(R.id.custom_title_main);
        title.setText(getString(R.string.app_name));

        fragmentArrayList.add(new quotes());
        fragmentArrayList.add(new home());
        fragmentArrayList.add(new wisdom());

        MainAdapter adapter = new MainAdapter(this, fragmentArrayList);

        viewPager2.setAdapter(adapter);

        viewPager2.setCurrentItem(1);
        bottomnavigationview.setSelectedItemId(R.id.homeNav);

        bottomnavigationview.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.quotesNav:
                    viewPager2.setCurrentItem(0);
                    break;
                case R.id.homeNav:
                    viewPager2.setCurrentItem(1);
                    break;
                case R.id.wisdomNav:
                    viewPager2.setCurrentItem(2);
                    break;
            }

            return true;
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomnavigationview.setSelectedItemId(R.id.quotesNav);
                        break;
                    case 1:
                        bottomnavigationview.setSelectedItemId(R.id.homeNav);
                        break;
                    case 2:
                        bottomnavigationview.setSelectedItemId(R.id.wisdomNav);
                        break;
                }
                super.onPageSelected(position);
            }
        });




    }

    @Override
    public void onBackPressed(){
        this.finishAffinity();
    }
}