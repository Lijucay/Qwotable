package com.lijukay.quotesAltDesign.fragments;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lijukay.quotesAltDesign.BuildConfig;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.activities.Information;
import com.lijukay.quotesAltDesign.service.InternetService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

public class home extends Fragment {

    int versionCode, versionCurrent, versionBeta;
    RequestQueue mRequestQueue;
    View v;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences betaSharedPreference, languageSharedPreference;
    public static String BroadcastStringForAction = "checkInternet";
    private IntentFilter mIntentFilter;
    boolean internet;
    String versionNameBeta, versionName, changelogBeta, changelogMessage, apkBeta, apkUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home, container, false);

        Intent serviceIntent = new Intent(requireContext(), InternetService.class);
        requireContext().startService(serviceIntent);

        mIntentFilter = new IntentFilter();
        //------Action of this IntentFilter: Checking the internet------//
        mIntentFilter.addAction(BroadcastStringForAction);
        internet = isOnline(requireActivity().getApplicationContext());

        swipeRefreshLayout = v.findViewById(R.id.homeSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(requireContext(), getString(R.string.toast_message_home), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                Cache cache = mRequestQueue.getCache();
                cache.clear();
                parseJSON();
                getLanguage();
            }, 2000);
        });

        betaSharedPreference = requireActivity().getSharedPreferences("Beta", 0);

        versionCurrent = BuildConfig.VERSION_CODE;

        mRequestQueue = Volley.newRequestQueue(requireContext());

        parseJSON();

        v.findViewById(R.id.information_card).setOnClickListener(v -> startActivity(new Intent(getActivity(), Information.class)));
        return v;
    }

    public final BroadcastReceiver InternetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastStringForAction)) {
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
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(InternetReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireContext().registerReceiver(InternetReceiver, mIntentFilter);
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

    private void parseJSON() {
        String url = "https://lijukay.github.io/PrUp/prUp.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("Qwotable");

                JSONObject jsonObject = jsonArray.getJSONObject(0);

                versionCode = jsonObject.getInt("versionsCode");
                versionBeta = jsonObject.getInt("versionsCodeBeta");
                versionNameBeta = jsonObject.getString("versionsNameBeta");
                versionName = jsonObject.getString("versionsName");
                changelogBeta = jsonObject.getString("changelogBeta");
                changelogMessage = jsonObject.getString("changelog");
                apkBeta = jsonObject.getString("apkUrlBeta");
                apkUrl = jsonObject.getString("apkUrl");



                if (versionCode > versionCurrent){
                    CardView updateCardView = v.findViewById(R.id.updateCardView);
                    updateCardView.setVisibility(View.VISIBLE);
                    TextView updateText = v.findViewById(R.id.update_message);
                    updateText.setText(getString(R.string.update_card_home_title));
                    updateCardView.setOnClickListener(v -> {
                        if (!internet) {
                            Toast.makeText(requireContext(), getString(R.string.no_internet_toast_message), Toast.LENGTH_SHORT).show();
                        } else {
                            new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                                    .setTitle(getString(R.string.updater_dialog_title))
                                    .setMessage(BuildConfig.VERSION_NAME +" -> " + versionName + "\n\n" +changelogMessage)
                                    .setIcon(R.drawable.ic_baseline_system_update_24)
                                    .setPositiveButton(getString(R.string.updater_positive_button), (dialog, which) -> InstallUpdate(requireActivity(), apkUrl, versionName))
                                    .setNeutralButton(getString(R.string.updater_negative_button), (dialog, which) -> dialog.dismiss())
                                    .show();
                        }

                    });
                } else if (versionBeta > versionCurrent && betaSharedPreference.getBoolean("beta", false)){
                    CardView updateCardView = v.findViewById(R.id.updateCardView);
                    updateCardView.setVisibility(View.VISIBLE);
                    TextView updateText = v.findViewById(R.id.update_message);
                    updateText.setText(getString(R.string.update_card_home_beta_title));
                    updateCardView.setOnClickListener(v -> {
                        if (!internet) {
                            Toast.makeText(requireContext(), getString(R.string.no_internet_toast_message), Toast.LENGTH_SHORT).show();
                        } else {
                            new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                                    .setTitle(getString(R.string.updater_dialog_title))
                                    .setMessage(BuildConfig.VERSION_NAME +" -> " + versionNameBeta + "\n\n" +changelogBeta)
                                    .setIcon(R.drawable.ic_baseline_system_update_24)
                                    .setPositiveButton(getString(R.string.updater_positive_button), (dialog, which) -> InstallUpdate(requireActivity(), apkBeta, versionNameBeta))
                                    .setNeutralButton(getString(R.string.updater_negative_button), (dialog, which) -> dialog.dismiss())
                                    .show();
                        }
                    });
                } else {
                    v.findViewById(R.id.updateCardView).setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }, Throwable::printStackTrace);
        mRequestQueue.add(request);
    }

    public static void InstallUpdate(Context context, String url, String versionName) {

        //------Set the destination as a string------//
        String destination = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + context.getString(R.string.app_name) + "." + versionName + ".apk";
        //------Set the file uri------//
        Uri fileUri = Uri.parse("file://" + destination);

        File file = new File(destination);

        if (file.exists()) //noinspection ResultOfMethodCallIgnored
            file.delete();

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setMimeType("application/vnd.android.package-archive");
        request.setTitle(context.getString(R.string.app_name) + " Update");
        request.setDescription(versionName);
        request.setDestinationUri(fileUri);

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {

                Uri apkFileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(destination));

                Intent install = new Intent(Intent.ACTION_VIEW);

                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.setDataAndType(apkFileUri, "application/vnd.android.package-archive");

                context.startActivity(install);
                context.unregisterReceiver(this);
            }
        };
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadManager.enqueue(request);
    }

}