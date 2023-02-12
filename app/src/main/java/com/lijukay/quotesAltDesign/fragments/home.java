package com.lijukay.quotesAltDesign.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.activities.Information;
import com.lijukay.quotesAltDesign.service.InternetService;

import java.util.Locale;

public class home extends Fragment {

    View v;
    SharedPreferences languageSharedPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home, container, false);

        Intent serviceIntent = new Intent(requireContext(), InternetService.class);
        requireContext().startService(serviceIntent);

        getLanguage();

        v.findViewById(R.id.information_card).setOnClickListener(v -> startActivity(new Intent(getActivity(), Information.class)));
        return v;
    }

    private void getLanguage() {
        languageSharedPreference = requireActivity().getSharedPreferences("Language", 0);

        String languageString = languageSharedPreference.getString("language", Locale.getDefault().getLanguage());

        Locale locale = new Locale(languageString);
        Locale.setDefault(locale);

        Resources resources = this.getResources();

        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}