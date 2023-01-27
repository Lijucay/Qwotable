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
import androidx.core.splashscreen.SplashScreen;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.elevation.SurfaceColors;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.adapter.SliderAdapter;

public class OnBoardingScreen extends AppCompatActivity {

    private ViewPager onBoarding;
    private LinearLayout linearLayout;
    private TextView[] mDots;
    Button previous, next;
    private int currentPage;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        int color = SurfaceColors.SURFACE_2.getColor(this);
        getWindow().setStatusBarColor(color);
        getWindow().setNavigationBarColor(color);


        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_screen);

        //Create a SharedPreference in MODE_PRIVATE that has the name FirstStart
        SharedPreferences firstStart = getSharedPreferences("FirstStart", 0);
        //Create a boolean that gets the boolean with the key "FirstTime?" which normally is true as the user opens the app for the first time
        boolean firstTime = firstStart.getBoolean("FirstTime?", true);
        if (!firstTime){ //If the value of first time equals false (! = is not, so !firstTime = is not firstTime [firstTime is false]),
            // the App automatically starts the MainActivity
            startActivity(new Intent(OnBoardingScreen.this, MainActivity.class));
        } else { //Else the Editor changes true to false so next time the user opens the app, it skips this activity as it is now !firstTime
            SharedPreferences.Editor editorFirst = firstStart.edit();
            editorFirst.putBoolean("FirstTime?", false);
            editorFirst.apply();
        }

        //next and previous buttons as well as the onBoarding ViewPager and linearLayout LinearLayout are found by their ID
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        onBoarding = findViewById(R.id.slideViewPager);
        linearLayout = findViewById(R.id.dotLayout);

        //Creating a new Object (Adapter)
        SliderAdapter sliderAdapter = new SliderAdapter(this);

        //set the adapter of onBoarding (ViewPager) to the sliderAdapter which was created before
        onBoarding.setAdapter(sliderAdapter);
        //set the OnPageChangeListener to viewListener (which is created later in this Class)
        onBoarding.addOnPageChangeListener(viewListener);

        //Set the action that happens when the buttons are clicked
        next.setOnClickListener(v -> onBoarding.setCurrentItem(currentPage + 1));
        previous.setOnClickListener(v -> onBoarding.setCurrentItem(currentPage - 1));

        //Call Method addDotsIndicator and give it 0 as position
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

    //Creating a method called addDotsIndicator which returns nothing (void)
    public void addDotsIndicator(int position){
        //Creating a new TextViewArray with the length of 7 (the count of pages that the onBo
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
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDotsIndicator(position);
            currentPage = position;

            if (position == 0){
                next.setEnabled(true);
                previous.setEnabled(false);
                previous.setVisibility(View.INVISIBLE);
                next.setText(getString(R.string.next));
                //Set the action that happens when the buttons are clicked
                next.setOnClickListener(v -> onBoarding.setCurrentItem(currentPage + 1));
            } else if (position == mDots.length -1){//As mDots.length starts as one but but the Array of pages starts with 0, we will get an OutOfBounds Exeption
                //So what we have to do is to make the get the length -1
                next.setEnabled(true);
                previous.setEnabled(true);
                previous.setVisibility(View.VISIBLE);
                next.setText(getString(R.string.start));
                next.setOnClickListener(v -> startActivity(new Intent(OnBoardingScreen.this, MainActivity.class)));
            } else {
                next.setEnabled(true);
                previous.setEnabled(true);
                previous.setVisibility(View.VISIBLE);
                next.setText(getString(R.string.next));
                //Set the action that happens when the buttons are clicked
                next.setOnClickListener(v -> onBoarding.setCurrentItem(currentPage + 1));
                previous.setOnClickListener(v -> onBoarding.setCurrentItem(currentPage - 1));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}