package com.lijukay.quotesAltDesign.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.elevation.SurfaceColors;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.adapter.SliderAdapter;

import java.util.Locale;

public class OnBoardingScreen extends AppCompatActivity {

    private ViewPager onBoarding;
    private LinearLayout linearLayout;
    private TextView[] mDots;
    Button previous, next;
    private int currentPage;
    public SharedPreferences language;
    public SharedPreferences.Editor languageeditor;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int color = SurfaceColors.SURFACE_2.getColor(this);
        getWindow().setStatusBarColor(color);
        getWindow().setNavigationBarColor(color);

        language = getSharedPreferences("Language", 0);
        languageeditor = language.edit();

        if (Locale.getDefault().getLanguage().equals("de")){
            languageeditor.putString("language", "de").apply();
        } else if (Locale.getDefault().getLanguage().equals("fr")){
            languageeditor.putString("language", "fr").apply();
        } else {
            languageeditor.putString("language", "en").apply();
        }


        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_screen);

        SharedPreferences firstStart = getSharedPreferences("FirstStart", 0);
        boolean firstTime = firstStart.getBoolean("FirstTime?", true);
        if (!firstTime){
            startActivity(new Intent(OnBoardingScreen.this, MainActivity.class));
        } else {
            SharedPreferences.Editor editorFirst = firstStart.edit();
            editorFirst.putBoolean("FirstTime?", false);
            editorFirst.apply();
        }

        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        onBoarding = findViewById(R.id.slideViewPager);
        linearLayout = findViewById(R.id.dotLayout);

        SliderAdapter sliderAdapter = new SliderAdapter(this);

        onBoarding.setAdapter(sliderAdapter);
        onBoarding.addOnPageChangeListener(viewListener);

        next.setOnClickListener(v -> onBoarding.setCurrentItem(currentPage + 1));
        previous.setOnClickListener(v -> onBoarding.setCurrentItem(currentPage - 1));

        addDotsIndicator(0);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        float scaleFactor = metrics.density;

        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        float smallestWidth = Math.min(widthDp, heightDp);

        if (smallestWidth >= 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        }
        else if (smallestWidth < 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void addDotsIndicator(int position){
        mDots = new TextView[7];
        linearLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(HtmlCompat.fromHtml("&#8226", HtmlCompat.FROM_HTML_MODE_LEGACY));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.md_theme_light_primaryContainer, getTheme()));
            linearLayout.addView(mDots[i]);
        }

        if (mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.md_theme_dark_primaryContainer, getTheme()));
        }
        linearLayout.setVisibility(View.GONE);
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {

            addDotsIndicator(position);
            currentPage = position;

            if (position == 0){
                next.setEnabled(true);
                previous.setEnabled(false);
                previous.setVisibility(View.INVISIBLE);
                next.setText(getString(R.string.next_button));
                next.setOnClickListener(v -> onBoarding.setCurrentItem(currentPage + 1));
            } else if (position == mDots.length -1){
                next.setEnabled(true);
                previous.setEnabled(true);
                previous.setVisibility(View.VISIBLE);
                next.setText(getString(R.string.start_button));
                next.setOnClickListener(v -> startActivity(new Intent(OnBoardingScreen.this, MainActivity.class)));
            } else {
                next.setEnabled(true);
                previous.setEnabled(true);
                previous.setVisibility(View.VISIBLE);
                next.setText(getString(R.string.next_button));
                next.setOnClickListener(v -> onBoarding.setCurrentItem(currentPage + 1));
                previous.setOnClickListener(v -> onBoarding.setCurrentItem(currentPage - 1));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}