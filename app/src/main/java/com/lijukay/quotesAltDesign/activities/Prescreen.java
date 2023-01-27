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
        setContentView(R.layout.activity_prescreen);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        startActivity(new Intent(this, OnBoardingScreen.class));
    }
}