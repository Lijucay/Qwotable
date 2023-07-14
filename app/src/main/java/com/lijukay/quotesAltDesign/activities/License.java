package com.lijukay.quotesAltDesign.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.adapter.LicenseAdapter;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;
import com.lijukay.quotesAltDesign.item.LicenseItem;

import java.util.ArrayList;

public class License extends AppCompatActivity implements RecyclerViewInterface {

    private String[] links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            setTheme(R.style.AppTheme);
        } else {
            switch (getSharedPreferences("Color", 0).getString("color", "defaultOrDynamic")) {
                case "red":
                    setTheme(R.style.AppThemeRed);
                    break;
                case "pink":
                    setTheme(R.style.AppThemePink);
                    break;
                case "green":
                    setTheme(R.style.AppThemeGreen);
                    break;
                default:
                    setTheme(R.style.AppTheme);
                    break;
            }
        }

        boolean tablet = getResources().getBoolean(R.bool.isTablet);

        setContentView(R.layout.activity_license);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);


        if (!tablet) ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.licenseRV), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.findViewById(R.id.licenseRV).setPadding(0,0,0,insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        }); else ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.licenseRV), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.findViewById(R.id.licenseRV).setPadding(0, insets.top, 0, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        MaterialToolbar toolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        RecyclerView recyclerView = findViewById(R.id.licenseRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        String[] titles = new String[]
                {
                        "Lottie animations by LottieFiles",
                        "RikkaX by RikkaApps",
                        "quotes by dwyl",
                        "Material Design 3",
                        "Material Icons"
                };

        String[] licenses = new String[]
                {
                        "Lottie Simple License (FL 9.13.21)",
                        "MIT License",
                        "GNU General Public License v2.0",
                        "Apache License, Version 2.0",
                        "Apache License, Version 2.0"
                };

        links = new String[]
                {
                        "https://lottiefiles.com/page/license",
                        "https://github.com/RikkaApps/RikkaX/blob/master/LICENSE",
                        "https://github.com/dwyl/quotes/blob/main/LICENSE",
                        "https://m3.material.io/",
                        "https://fonts.google.com/icons"
                };

        ArrayList<LicenseItem> items = new ArrayList<>();

        for (int i = 0; i <titles.length; i++) {
            items.add(new LicenseItem(titles[i], licenses[i], links[i]));
        }

        LicenseAdapter adapter = new LicenseAdapter(this, items, this);

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(int position, String type, MaterialButton mbid) {
        if (type.equals("license")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(links[position])));
        }
    }
}