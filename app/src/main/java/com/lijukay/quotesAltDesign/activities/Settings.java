package com.lijukay.quotesAltDesign.activities;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.FileProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.lijukay.quotesAltDesign.BuildConfig;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.service.InternetService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

import rikka.material.preference.MaterialSwitchPreference;

public class Settings extends AppCompatActivity {

    public static final String BroadcastStringForAction = "checkInternet";
    static Intent starterIntent;
    //static String versionNameBeta, versionName, apkUrl, apkBeta, changelogMessage, changelogBeta
    static String languageCode, colorS;
    //static int versionC, versionA, versionB;
    static SharedPreferences betaSP, language, sharedPreferencesColors;
    static SharedPreferences.Editor betaEditor, languageEditor, colorEditor;
    //private static RequestQueue mRequestQueueU;
    //static boolean betaA = false, updateStatus = false, internet;

    @SuppressLint("StaticFieldLeak")

    /*public final BroadcastReceiver InternetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastStringForAction)) {
                internet = intent.getStringExtra("online_status").equals("true");
            }
        }
    };*/
    //------Creating the SwipeRefreshLayout------//
    private SwipeRefreshLayout swipeRefreshLayout;
    //------Creating an IntentFilter------//
    private IntentFilter mIntentFilter;

    /*private static void parseJSONVersion() {
        String url = "https://lijukay.github.io/PrUp/prUp.json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, jsonObject -> {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("Qwotable");

                for (int a = 0; a < jsonArray.length(); a++) {

                    JSONObject v = jsonArray.getJSONObject(a);

                    versionC = BuildConfig.VERSION_CODE;
                    versionA = v.getInt("versionsCode");
                    versionB = v.getInt("versionsCodeBeta");
                    apkUrl = v.getString("apkUrl");
                    apkBeta = v.getString("apkUrlBeta");
                    changelogMessage = v.getString("changelog");
                    changelogBeta = v.getString("changelogBeta");
                    versionName = v.getString("versionsName");
                    versionNameBeta = v.getString("versionsNameBeta");
                }

                if (versionB > versionC) {
                    betaA = true;
                }

                if (versionA > versionC) {
                    betaA = false;
                    updateStatus = true;
                } else {
                    updateStatus = false;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        mRequestQueueU.add(jsonObjectRequest);


    }*/

    /*public static void InstallUpdate(Context context, String url, String versionName) {

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
    }*/

    @SuppressLint({"InflateParams", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        betaSP = getSharedPreferences("Beta", 0);
        language = getSharedPreferences("Language", 0);
        sharedPreferencesColors = getSharedPreferences("Colors", 0);
        betaEditor = betaSP.edit();
        languageEditor = language.edit();
        colorEditor = sharedPreferencesColors.edit();

        languageCode = language.getString("language", Locale.getDefault().getLanguage());
        colorS = sharedPreferencesColors.getString("color", "red");

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            setTheme(R.style.AppTheme);
        } else {
            switch (sharedPreferencesColors.getString("color", "red")){
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



        //------Set the contentView to the layout of settings_activity------//
        setContentView(R.layout.settings_activity);

        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(materialToolbar);

        materialToolbar.setNavigationOnClickListener(v -> onBackPressed());

        int color = SurfaceColors.SURFACE_2.getColor(this);
        getWindow().setStatusBarColor(color);
        getWindow().setNavigationBarColor(color);

        if (smallestWidth >= 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        else if (smallestWidth < 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //------IF ANYONE KNOWS WHAT THE CONDITION OF THE IF LOOP HERE IS, PWEASE EXPLAIN ME!------//
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        }

        ActionBar actionBar = getSupportActionBar();
        //------Check if actionBar is null------//
        if (actionBar != null) {
            //------IF ANYONE KNOWS WHAT THIS THING DOES, PWEASE EXPLAIN. I HAVE NO IDEA!------//
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /*mRequestQueueU = Volley.newRequestQueue(this);
        swipeRefreshLayout = findViewById(R.id.settingsSRL);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(this, getString(R.string.toast_message_settings), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                Cache cache = mRequestQueueU.getCache();
                cache.clear();
                parseJSONVersion();
            }, 2000);
        });*/

        //------getIntent() returns the intent that started the activity------//
        starterIntent = getIntent();

        mIntentFilter = new IntentFilter();
        //------Action of this IntentFilter: Checking the internet------//
        mIntentFilter.addAction(BroadcastStringForAction);
        //------Referring to the class where the service is written down and starting the service------//
        Intent serviceIntent = new Intent(this, InternetService.class);
        startService(serviceIntent);
        //------Checking if the Application is online------//
        //internet = isOnline(getApplicationContext());

        //parseJSONVersion();
    }

    /*public boolean isOnline(Context c) {
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
    }*/

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private Uri githubQwotableRequest, googleFormsQwotableRequest, googleFormsBugReport, gitHubBugReport, googleFormsFeatureRequest, githubFeatureRequest, googleFormsFeedback,githubFeedback;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            CharSequence[] items = {
                    "Deutsch",
                    "English",
                    "Français"
            };
            CharSequence[] colors;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
                colors =
                        new CharSequence[]
                                {
                                        getString(R.string.color_red),
                                        getString(R.string.color_pink),
                                        getString(R.string.color_green)
                                };
            } else {
                colors =
                        new CharSequence[]
                                {
                                        getString(R.string.color_dynamic),
                                        getString(R.string.color_pink),
                                        getString(R.string.color_green)
                                };
            }
            int checkedColor = 0;

            int checkedItem = -1;

            githubQwotableRequest = Uri.parse("https://github.com/Lijukay/Qwotable/issues/new?assignees=&labels=qwotable+request&template=qwotable-request.md&title=Qwotable+request");
            googleFormsBugReport = Uri.parse("https://forms.gle/zXD69gpYWtghXAuF6");
            googleFormsQwotableRequest = Uri.parse("https://forms.gle/VKWUBDPPiiFkLcjRA");
            gitHubBugReport = Uri.parse("https://github.com/Lijukay/Qwotable/issues/new?assignees=&labels=bug+report&template=bug_report.md&title=Bug+report");
            googleFormsFeatureRequest = Uri.parse("https://forms.gle/KZhEASuaQuBQtC9k6");
            githubFeatureRequest = Uri.parse("https://github.com/Lijukay/Qwotable/issues/new?assignees=&labels=feature+request&template=feature_request.md&title=Feature+request");
            googleFormsFeedback = Uri.parse("https://forms.gle/iCaaL1Nuck7u3bfE8");
            githubFeedback = Uri.parse("https://github.com/Lijukay/Qwotable/issues/new?assignees=&labels=feedback&template=feedback.md&title=Feedback");


            //parseJSONVersion();
            Preference color = findPreference("colors");
            assert color != null;
            String colorSummary = getString(R.string.color_red);
            switch (sharedPreferencesColors.getString("color", "red")){
                case "red":
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
                        colorSummary = getString(R.string.color_red);
                    } else {
                        colorSummary = getString(R.string.color_dynamic);
                    }
                    break;
                case "pink":
                    checkedColor = 1;
                    colorSummary = getString(R.string.color_pink);
                    break;
                case "green":
                    checkedColor = 2;
                    colorSummary = getString(R.string.color_green);
                    break;
            }
            color.setSummary(getString(R.string.color_preference_summary) + " " + colorSummary);


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                color.setVisible(false);
            } else {
                color.setVisible(true);
                int finalCheckedColor = checkedColor;
                color.setOnPreferenceClickListener(preference -> {
                    new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                            .setTitle(getString(R.string.colors_preference_title))
                            .setSingleChoiceItems(colors, finalCheckedColor, (dialog, which) -> {
                                switch (which){
                                    case 0:
                                        colorEditor.putString("color", "red");
                                        break;
                                    case 1:
                                        colorEditor.putString("color", "pink");
                                        break;
                                    case 2:
                                        colorEditor.putString("color", "green");
                                }
                            })
                            .setPositiveButton(getString(R.string.positive_button_text_set), (dialog, which) -> {
                                colorEditor.apply();
                                requireActivity().startActivity(new Intent(requireActivity(), MainActivity.class));
                                requireActivity().overridePendingTransition(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out);
                                requireActivity().finishAffinity();
                            })
                            .setNeutralButton(getString(R.string.neutral_button_text_cancel), (dialog, which) -> dialog.dismiss())
                            .setIcon(R.drawable.ic_baseline_colors_24)
                            .show();
                    return false;
                });
            }


            /*MaterialSwitchPreference beta = findPreference("beta");
            assert beta != null;

            if (beta.isChecked()){
                beta.setSummary(getString(R.string.beta_switch_preference_summary_positive));
            } else {
                beta.setSummary(getString(R.string.beta_switch_preference_summary_negative));
            }

            beta.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!beta.isChecked()){
                    beta.setSummary(getString(R.string.beta_switch_preference_summary_positive));
                } else {
                    beta.setSummary(getString(R.string.beta_switch_preference_summary_negative));
                }
                betaEditor.putBoolean("beta", !beta.isChecked());
                betaEditor.apply();

                return true;
            });*/


            Preference qwrequest = findPreference("qwrequest");
            assert qwrequest != null;
            qwrequest.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.qwotable_request_dialog_title))
                        .setMessage(getString(R.string.qwotable_request_dialog_message))
                        .setIcon(R.drawable.ic_baseline_question_mark_24)
                        .setPositiveButton(getString(R.string.google_forms_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, googleFormsQwotableRequest)))
                        .setNegativeButton(getString(R.string.github_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, githubQwotableRequest)))
                        .setNeutralButton(getString(R.string.neutral_button_text_cancel), (dialog, which) -> dialog.dismiss())
                        .show();


                return false;
            });


            switch (language.getString("language", Locale.getDefault().getLanguage())){
                case "de":
                    checkedItem = 0;
                    break;
                case "en":
                    checkedItem = 1;
                    break;
                case "fr":
                    checkedItem = 2;
                    break;
            }


            Preference language = findPreference("language");
            assert language != null;
            //------Getting sharedPreferencesLanguage to display current language------//
            SharedPreferences sharedPreferencesLanguage = requireActivity().getSharedPreferences("Language", 0);

            switch (sharedPreferencesLanguage.getString("language", Locale.getDefault().getLanguage())) {
                case "de":
                case "en":
                case "fr":
                    language.setSummary(getString(R.string.language_preference_summary_language_supported));
                    break;
                default:
                    language.setSummary(getString(R.string.language_preference_summary_language_unsupported));
                    break;
            }


            int finalCheckedItem = checkedItem;
            language.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.language_preference_title))
                        .setSingleChoiceItems(items, finalCheckedItem, (dialog, which) -> {
                            switch (which){
                                case 0:
                                    languageEditor.putString("language", "de");
                                    break;
                                case 1:
                                    languageEditor.putString("language", "en");
                                    break;
                                case 2:
                                    languageEditor.putString("language", "fr");
                                    break;
                            }
                        })
                        .setPositiveButton(getString(R.string.positive_button_text_set), (dialog, which) -> {
                            languageEditor.apply();
                            requireActivity().startActivity(new Intent(requireActivity(), MainActivity.class));
                            requireActivity().overridePendingTransition(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out);
                            requireActivity().finishAffinity();
                        })
                        .setNeutralButton(getString(R.string.neutral_button_text_cancel), (dialog, which) -> dialog.dismiss())
                        .setIcon(R.drawable.ic_baseline_language_24)
                        .show();
                return false;
            });

            /*Preference updater = findPreference("updateCheck");

            assert updater != null;
            updater.setOnPreferenceClickListener(preference -> {

                if (!internet) {
                    Toast.makeText(requireContext(), getString(R.string.no_internet_toast_message), Toast.LENGTH_SHORT).show();
                } else  if (!updateStatus && !betaA && betaSP.getBoolean("beta", false) || !updateStatus && !betaSP.getBoolean("beta", false)){
                    Toast.makeText(requireContext(), getString(R.string.no_update_toast_message), Toast.LENGTH_SHORT).show();
                } else if (updateStatus){
                    new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                            .setTitle(getString(R.string.updater_dialog_title))
                            .setMessage(BuildConfig.VERSION_NAME +" -> " + versionName + "\n\n" +changelogMessage)
                            .setIcon(R.drawable.ic_baseline_system_update_24)
                            .setPositiveButton(getString(R.string.updater_positive_button), (dialog, which) -> InstallUpdate(requireActivity(), apkUrl, versionName))
                            .setNeutralButton(getString(R.string.updater_negative_button), (dialog, which) -> dialog.dismiss())
                            .show();
                } else if (betaA && betaSP.getBoolean("beta", false)){
                    new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                            .setTitle(getString(R.string.updater_dialog_title))
                            .setMessage(BuildConfig.VERSION_NAME +" -> " + versionNameBeta + "\n\n" +changelogBeta)
                            .setIcon(R.drawable.ic_baseline_system_update_24)
                            .setPositiveButton(R.string.updater_positive_button, (dialog, which) -> InstallUpdate(requireActivity(), apkBeta, versionNameBeta))
                            .setNeutralButton(R.string.updater_negative_button, (dialog, which) -> dialog.dismiss())
                            .show();

                }

                return false;

            });*/

            Preference bug_report = findPreference("reportBug");

            assert bug_report != null;
            bug_report.setOnPreferenceClickListener(preference -> {
                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.report_a_bug_dialog_title))
                        .setMessage(getString(R.string.report_a_bug_dialog_message))
                        .setIcon(R.drawable.ic_baseline_bug_report_24)
                        .setPositiveButton(getString(R.string.google_forms_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, googleFormsBugReport)))
                        .setNegativeButton(getString(R.string.github_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, gitHubBugReport)))
                        .setNeutralButton(getString(R.string.neutral_button_text_cancel), (dialog, which) -> dialog.dismiss())
                        .show();
                return false;
            });


            Preference feature_suggestion = findPreference("feature");
            assert feature_suggestion != null;
            feature_suggestion.setOnPreferenceClickListener(preference -> {
                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.request_a_feature_dialog_title))
                        .setMessage(getString(R.string.request_a_feature_dialog_message))
                        .setIcon(R.drawable.ic_baseline_auto_awesome_24)
                        .setPositiveButton(getString(R.string.google_forms_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, googleFormsFeatureRequest)))
                        .setNegativeButton(getString(R.string.github_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, githubFeatureRequest)))
                        .setNeutralButton(getString(R.string.neutral_button_text_cancel), (dialog, which) -> dialog.dismiss())
                        .show();
                return false;
            });

            Preference share_app = findPreference("share");
            assert share_app != null;
            share_app.setOnPreferenceClickListener(preference -> {

                Intent shareText = new Intent();

                shareText.setAction(Intent.ACTION_SEND);
                shareText.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
                shareText.setType("text/plain");

                Intent sendText = Intent.createChooser(shareText, null);

                startActivity(sendText);

                return false;
            });

            Preference feedback = findPreference("feedback");

            assert feedback != null;
            feedback.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.feedback_dialog_title))
                        .setMessage(getString(R.string.feedback_dialog_message))
                        .setIcon(R.drawable.ic_baseline_feedback_24)
                        .setPositiveButton(getString(R.string.google_forms_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, googleFormsFeedback)))
                        .setNegativeButton(getString(R.string.github_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, githubFeedback)))
                        .setNeutralButton(getString(R.string.neutral_button_text_cancel), (dialog, which) -> dialog.dismiss())
                        .show();

                return false;

            });

            Preference app_permission = findPreference("permission");
            assert app_permission != null;
            app_permission.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.permission_dialog_title))
                        .setMessage(getString(R.string.permission_dialog_message))
                        .setIcon(R.drawable.ic_baseline_gpp_good_24)
                        .setPositiveButton(getString(R.string.positive_button_okay_text), (dialog, which) -> dialog.dismiss())
                        .show();

                return false;
            });

            Preference privacy_policy = findPreference("policy");

            assert privacy_policy != null;
            privacy_policy.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.privacy_dialog_title))
                        .setMessage(getString(R.string.privacy_dialog_message))
                        .setIcon(R.drawable.ic_baseline_gpp_maybe_24)
                        .setPositiveButton(getString(R.string.positive_button_okay_text), (dialog, which) -> dialog.dismiss())
                        .show();


                return false;
            });

            Preference license = findPreference("license");

            assert license != null;
            license.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.licenses_dialog_title))
                        .setMessage(getString(R.string.licenses_dialog_message))
                        .setIcon(R.drawable.ic_baseline_local_police_24)
                        .setPositiveButton(getString(R.string.positive_button_okay_text), (dialog, which) -> dialog.dismiss())
                        .show();

                return false;
            });

            Preference wiki = findPreference("wiki");

            assert wiki != null;
            wiki.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.wiki_dialog_title))
                        .setMessage(getString(R.string.wiki_dialog_message))
                        .setIcon(R.drawable.ic_baseline_web_stories_24)
                        .setPositiveButton(getString(R.string.wiki_button), (dialog, which) -> {
                            String url = "https://lijukay.gitbook.io/qwotable/";
                            CustomTabColorSchemeParams colorSchemeParams = new CustomTabColorSchemeParams.Builder().setToolbarColor(getResources().getColor(R.color.md_theme_dark_onPrimary, requireActivity().getTheme())).build();
                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                            builder.setCloseButtonPosition(CustomTabsIntent.CLOSE_BUTTON_POSITION_DEFAULT);
                            builder.setStartAnimations(requireContext(), rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out);
                            builder.setExitAnimations(requireContext(), rikka.core.R.anim.fade_out, rikka.core.R.anim.fade_in);
                            builder.setDefaultColorSchemeParams(colorSchemeParams);
                            CustomTabsIntent customTabsIntent = builder.build();
                            customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
                        })
                        .setNeutralButton(getString(R.string.neutral_button_text_cancel), (dialog, which) -> dialog.dismiss())
                        .show();
                return false;
            });
        }
    }
}