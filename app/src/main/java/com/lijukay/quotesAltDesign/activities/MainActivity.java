package com.lijukay.quotesAltDesign.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigationrail.NavigationRailView;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.adapter.MainAdapter;
import com.lijukay.quotesAltDesign.fragments.home;
import com.lijukay.quotesAltDesign.fragments.quotes;
import com.lijukay.quotesAltDesign.fragments.wisdom;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomnavigationview;
    private NavigationRailView navigationRailView;
    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private ViewPager2 viewPager2;
    private boolean tablet;

    public SharedPreferences languageSharedPreference, colorSharedPreference;

    @SuppressLint({"NonConstantResourceId", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languageSharedPreference = getSharedPreferences("Language", 0);
        Locale locale = new Locale(languageSharedPreference.getString("language", Locale.getDefault().getLanguage()));
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        colorSharedPreference = getSharedPreferences("Colors", 0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            setTheme(R.style.AppTheme);
        } else {
            switch (colorSharedPreference.getString("color", "red")) {
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
        }

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
        } else if (smallestWidth < 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        setContentView(R.layout.activity_main);



        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(materialToolbar);

        int color = SurfaceColors.SURFACE_2.getColor(this);
        getWindow().setStatusBarColor(color);
        getWindow().setNavigationBarColor(color);
        tablet = getResources().getBoolean(R.bool.isTablet);

        if (!tablet || this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomnavigationview = findViewById(R.id.bottom_navigation);
        } else {
            navigationRailView = findViewById(R.id.navigation_rail);
            navigationRailView.setBackgroundColor(color);
        }

        viewPager2 = findViewById(R.id.viewPager);

        fragmentArrayList.add(new quotes());
        fragmentArrayList.add(new home());
        fragmentArrayList.add(new wisdom());

        MainAdapter adapter = new MainAdapter(this, fragmentArrayList);

        viewPager2.setAdapter(adapter);

        viewPager2.setCurrentItem(1);

        if (!tablet || this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            bottomnavigationview.setSelectedItemId(R.id.homeNav);

            bottomnavigationview.setOnItemSelectedListener(item -> {

                switch (item.getItemId()) {
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
        } else {
            navigationRailView.setSelectedItemId(R.id.homeNav);

            navigationRailView.setOnItemSelectedListener(item -> {
                switch (item.getItemId()) {
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
        }


        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        if (!tablet || MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            bottomnavigationview.setSelectedItemId(R.id.quotesNav);
                        } else {
                            navigationRailView.setSelectedItemId(R.id.quotesNav);
                        }
                        break;
                    case 1:
                        if (!tablet || MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            bottomnavigationview.setSelectedItemId(R.id.homeNav);
                        } else {
                            navigationRailView.setSelectedItemId(R.id.homeNav);
                        }
                        break;
                    case 2:
                        if (!tablet || MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            bottomnavigationview.setSelectedItemId(R.id.wisdomNav);
                        } else {
                            navigationRailView.setSelectedItemId(R.id.wisdomNav);
                        }
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settingsMenu) {
            Settings();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void Settings() {
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}