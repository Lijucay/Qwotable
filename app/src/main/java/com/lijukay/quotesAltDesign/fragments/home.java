package com.lijukay.quotesAltDesign.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.service.InternetService;

import java.util.Locale;

public class home extends Fragment {

    View v;
    SharedPreferences languageSharedPreference;
    boolean tablet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home, container, false);

        Intent serviceIntent = new Intent(requireContext(), InternetService.class);
        requireContext().startService(serviceIntent);

        getLanguage();

        tablet = getResources().getBoolean(R.bool.isTablet);

        if (!tablet) ViewCompat.setOnApplyWindowInsetsListener(v.findViewById(R.id.nsv), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.findViewById(R.id.nsv).setPadding(0,0,0,insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        }); else ViewCompat.setOnApplyWindowInsetsListener(v.findViewById(R.id.nsv), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.findViewById(R.id.nsv).setPadding(0, insets.top, 0, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });
        return v;
    }

    private void getLanguage() {
        languageSharedPreference = requireActivity().getSharedPreferences("Language", 0);

        String languageString = languageSharedPreference.getString("language", "en");

        Locale locale = new Locale(languageString);
        Locale.setDefault(locale);

        Resources resources = this.getResources();

        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}