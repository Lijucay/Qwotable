package com.lijukay.quotesAltDesign.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigation.NavigationView;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.fragments.AddOwnQuotes;
import com.lijukay.quotesAltDesign.fragments.Information;
import com.lijukay.quotesAltDesign.fragments.dwyl_quotes;
import com.lijukay.quotesAltDesign.fragments.home;
import com.lijukay.quotesAltDesign.fragments.quotes;
import com.lijukay.quotesAltDesign.fragments.wisdom;
import com.lijukay.quotesAltDesign.service.InternetService;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Navigation
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    //Preferences
    public SharedPreferences languageSharedPreference, colorSharedPreference;

    //Internet
    public static String BroadCastStringForAction = "checkInternet";
    boolean internet;
    private IntentFilter mIntentFilter;
    MaterialToolbar materialToolbar;

    @SuppressLint({"NonConstantResourceId", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get language
        languageSharedPreference = getSharedPreferences("Language", 0);
        //set language to what is saved
        Locale locale = new Locale(languageSharedPreference.getString("language", "en"));
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        //get and set color theme
        colorSharedPreference = getSharedPreferences("Colors", 0);
        //check whether the Android version is smaller than Android Q (Android 10)
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

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        Intent serviceIntent = new Intent(this, InternetService.class);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        startService(serviceIntent);
        internet = isOnline(getApplicationContext());


        boolean tablet = getResources().getBoolean(R.bool.isTablet);
        navigationView = findViewById(R.id.navigation_view);



        if (!tablet || this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            materialToolbar = findViewById(R.id.top_app_bar);
            setSupportActionBar(materialToolbar);
            drawerLayout = findViewById(R.id.drawer_layout);
            materialToolbar.setNavigationOnClickListener(v -> drawerLayout.open());
        }

        navigationView.setCheckedItem(R.id.home_item);

        getSupportFragmentManager().beginTransaction().setCustomAnimations(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out).replace(R.id.fragment_container, new home()).commit();

        navigationView.setNavigationItemSelectedListener(item -> {
            if(!tablet){
                drawerLayout.close();
            }

            switch (item.getItemId()){
                case R.id.home_item:
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out).replace(R.id.fragment_container, new home()).commit();
                    navigationView.setCheckedItem(R.id.home_item);
                    if(!tablet) materialToolbar.setTitle(getString(R.string.app_name));
                    break;
                case R.id.quote_item:
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out).replace(R.id.fragment_container, new quotes()).commit();
                    navigationView.setCheckedItem(R.id.quote_item);
                    if(!tablet) materialToolbar.setTitle(getString(R.string.quotes_item));
                    break;
                case R.id.wisdom_item:
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out).replace(R.id.fragment_container, new wisdom()).commit();
                    navigationView.setCheckedItem(R.id.wisdom_item);
                    if(!tablet) materialToolbar.setTitle(R.string.wisdom_item);
                    break;
                case R.id.information_item:
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out).replace(R.id.fragment_container, new Information()).commit();
                    navigationView.setCheckedItem(R.id.information_item);
                    if(!tablet) materialToolbar.setTitle(getString(R.string.information_title));
                    break;
                case R.id.own_quotes_item:
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out).replace(R.id.fragment_container, new AddOwnQuotes()).commit();
                    navigationView.setCheckedItem(R.id.own_quotes_item);
                    if(!tablet) materialToolbar.setTitle("My Quotes");
                    break;
                case R.id.dwyl_quotes_item:
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out).replace(R.id.fragment_container, new dwyl_quotes()).commit();
                    navigationView.setCheckedItem(R.id.dwyl_quotes_item);
                    if(!tablet) materialToolbar.setTitle(getString(R.string.by_dwyl));
                    break;
            }
            return false;
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

    public final BroadcastReceiver InternetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadCastStringForAction)) {
                internet = intent.getStringExtra("online_status").equals("true");
            }
        }
    };

    public boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(InternetReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(InternetReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(InternetReceiver, mIntentFilter);
    }

}