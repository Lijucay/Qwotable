package com.lijukay.quotesAltDesign.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Bundle;

import com.lijukay.quotesAltDesign.R;

public class Prescreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);

        setContentView(R.layout.activity_prescreen);
        startActivity(new Intent(this, OnBoardingScreen.class));
    }
}