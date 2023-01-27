package com.lijukay.quotesAltDesign.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
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
    static String versionNameBeta, versionName, apkUrl, apkBeta, changelogMessage, changelogBeta, languageCode, colorS;
    static int versionC, versionA, versionB;
    static SharedPreferences betaSP, language, color;
    static SharedPreferences.Editor betaEditor, languageEditor, colorEditor;
    private static RequestQueue mRequestQueueU;
    static boolean betaA = false, updateStatus = false, internet;

    @SuppressLint("StaticFieldLeak")

    public final BroadcastReceiver InternetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastStringForAction)) {
                internet = intent.getStringExtra("online_status").equals("true");
            }
        }
    };
    //------Creating the SwipeRefreshLayout------//
    private SwipeRefreshLayout swipeRefreshLayout;
    //------Creating an IntentFilter------//
    private IntentFilter mIntentFilter;

    private static void parseJSONVersion() {
        String urlU = "https://lijukay.github.io/PrUp/prUp.json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlU, null, jsonObject -> {
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

    public static Intent composeEmail(String addresses, String subject, String messageE) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + addresses));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, messageE);
        return intent;
    }

    @SuppressLint({"InflateParams", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        betaSP = getSharedPreferences("Beta", 0);
        language = getSharedPreferences("Language", 0);
        color = getSharedPreferences("Colors", 0);
        //------make the SharedPreference.Editor editing the SharedPreference with the variable-name "betaSP"------//
        betaEditor = betaSP.edit();
        languageEditor = language.edit();
        colorEditor = color.edit();

        languageCode = language.getString("language", Locale.getDefault().getLanguage());
        colorS = color.getString("color", "red");

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            setTheme(R.style.AppTheme);
        } else {
            switch (color.getString("color", "red")){
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

        mRequestQueueU = Volley.newRequestQueue(this);
        swipeRefreshLayout = findViewById(R.id.settingsSRL);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(this, getString(R.string.refresh_message), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                Cache cache = mRequestQueueU.getCache();
                cache.clear();
                parseJSONVersion();
            }, 2000);
        });

        //------getIntent() returns the intent that started the activity------//
        starterIntent = getIntent();

        mIntentFilter = new IntentFilter();
        //------Action of this IntentFilter: Checking the internet------//
        mIntentFilter.addAction(BroadcastStringForAction);
        //------Referring to the class where the service is written down and starting the service------//
        Intent serviceIntent = new Intent(this, InternetService.class);
        startService(serviceIntent);
        //------Checking if the Application is online------//
        internet = isOnline(getApplicationContext());

        parseJSONVersion();
    }

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

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private Button negative;
        private ConstraintLayout layout;
        private Uri telegram;
        private String email;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            //int requestCode = 100; //TODO: Ask if plan works on older android devices or if it causes errors

            CharSequence[] items = {
                    getString(R.string.german),
                    getString(R.string.english),
                    getString(R.string.french)
            };
            CharSequence[] colors;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
                colors =
                        new CharSequence[]
                                {
                                        "Red",
                                        "Pink",
                                        "Green"
                                };
            } else {
                colors =
                        new CharSequence[]
                                {
                                        "Dynamic (Follows system)",
                                        "Pink",
                                        "Green"
                                };
            }
            int checkedColor = 0;

            int checkedItem = -1;

            telegram = Uri.parse("https://t.me/Lijukay");

            email = "luca.krumminga@gmail.com";


            parseJSONVersion();

            switch (color.getString("color", "red")){
                case "red":
                    break;
                case "pink":
                    checkedColor = 1;
                    break;
                case "green":
                    checkedColor = 2;
                    break;
            }

            Preference color = findPreference("colors");
            assert color != null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                color.setVisible(false);
            } else {
                color.setVisible(true);
                int finalCheckedColor = checkedColor;
                color.setOnPreferenceClickListener(preference -> {

                    new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                            .setTitle(getString(R.string.color_theme_title))
                                    .setSingleChoiceItems(colors, finalCheckedColor, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
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
                                        }
                                    })
                            .setPositiveButton("Okay", (dialog, which) -> {
                                colorEditor.apply();
                                requireActivity().startActivity(new Intent(requireActivity(), MainActivity.class));
                                requireActivity().overridePendingTransition(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out);
                                requireActivity().finishAffinity();
                            })
                            .setNeutralButton(getString(R.string.cancel_button), (dialog, which) -> dialog.dismiss())
                            .show();
                    return false;
                });
            }


            MaterialSwitchPreference beta = findPreference("beta");
            assert beta != null;

            if (beta.isChecked()){
                beta.setSummary("You want to get Beta updates");
            } else {
                beta.setSummary("You don't want to get Beta updates");
            }

            beta.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!beta.isChecked()){
                    beta.setSummary("You want to get Beta updates");
                } else {
                    beta.setSummary("You don't want to get Beta updates");
                }
                betaEditor.putBoolean("beta", !beta.isChecked());
                betaEditor.apply();

                return true;
            });


            Preference qwrequest = findPreference("qwrequest");
            assert qwrequest != null;
            qwrequest.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.qwrequest_title))
                        .setMessage(getString(R.string.qwrequest_message))
                        .setIcon(R.drawable.ic_baseline_question_mark_24)
                        .setPositiveButton(getString(R.string.telegram_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, telegram)))
                        .setNegativeButton(getString(R.string.email_button), (dialog, which) -> startActivity(composeEmail(email, getString(R.string.feature_suggestion_mail_subject), getString(R.string.feature_suggestion_mail_message))))
                        .setNeutralButton(getString(R.string.cancel_button), (dialog, which) -> dialog.dismiss())
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
            String lang = sharedPreferencesLanguage.getString("language", Locale.getDefault().getLanguage());

            switch (lang) {
                case "de":
                case "en":
                case "fr":
                    language.setSummary(R.string.lande);
                    break;
                default:
                    language.setSummary(R.string.notsupported);
                    break;
            }

            int finalCheckedItem = checkedItem;
            language.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.language_title))
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
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                languageEditor.apply();
                                requireActivity().startActivity(new Intent(requireActivity(), MainActivity.class));
                                requireActivity().overridePendingTransition(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out);
                                requireActivity().finishAffinity();
                            }
                        })
                                        .setNeutralButton(getString(R.string.cancel_button), (dialog, which) -> dialog.dismiss())
                                                .show();
                return false;
            });

            Preference updater = findPreference("updateCheck");

            assert updater != null;
            updater.setOnPreferenceClickListener(preference -> {

                if (!internet) {
                    Toast.makeText(requireContext(), getString(R.string.cant_check_for_updates), Toast.LENGTH_SHORT).show();
                } else  if (!updateStatus && !betaA && betaSP.getBoolean("beta", false) || !updateStatus && !betaSP.getBoolean("beta", false)){
                    Toast.makeText(requireContext(), getString(R.string.no_update_available), Toast.LENGTH_SHORT).show();
                } else if (updateStatus){
                    new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                            .setTitle(getString(R.string.update_title))
                                    .setMessage(BuildConfig.VERSION_NAME +" -> " + versionName + "\n\n" +changelogMessage)
                                            .setIcon(R.drawable.ic_baseline_system_update_24)
                                                    .setPositiveButton(getString(R.string.update_button), (dialog, which) -> InstallUpdate(requireActivity(), apkUrl, versionName))
                                                            .setNeutralButton(getString(R.string.cancel_button), (dialog, which) -> dialog.dismiss())
                                                                    .show();

                } else if (betaA && betaSP.getBoolean("beta", false)){
                    new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                            .setTitle(getString(R.string.update_title))
                            .setMessage(BuildConfig.VERSION_NAME +" -> " + versionNameBeta + "\n\n" +changelogBeta)
                            .setIcon(R.drawable.ic_baseline_system_update_24)
                            .setPositiveButton(getString(R.string.update_button), (dialog, which) -> InstallUpdate(requireActivity(), apkBeta, versionNameBeta))
                            .setNeutralButton(getString(R.string.cancel_button), (dialog, which) -> dialog.dismiss())
                            .show();

                }

                return false;

            });

            Preference bug_report = findPreference("reportBug");

            assert bug_report != null;
            bug_report.setOnPreferenceClickListener(preference -> {
                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.bug_report_title))
                        .setMessage(getString(R.string.bug_report_message))
                        .setIcon(R.drawable.ic_baseline_bug_report_24)
                        .setPositiveButton(getString(R.string.telegram_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, telegram)))
                        .setNegativeButton(getString(R.string.email_button), (dialog, which) -> startActivity(composeEmail(email, getString(R.string.bug_report_mail_subject), getString(R.string.bug_report_mail_message))))
                        .setNeutralButton(getString(R.string.cancel_button), (dialog, which) -> dialog.dismiss())
                        .show();
                return false;
            });


            Preference feature_suggestion = findPreference("feature");
            assert feature_suggestion != null;
            feature_suggestion.setOnPreferenceClickListener(preference -> {
                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.feature_suggestion_title))
                        .setMessage(getString(R.string.feature_suggestion_message))
                        .setIcon(R.drawable.ic_baseline_auto_awesome_24)
                        .setPositiveButton(getString(R.string.telegram_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, telegram)))
                        .setNegativeButton(getString(R.string.email_button), (dialog, which) -> startActivity(composeEmail(email, getString(R.string.feature_suggestion_mail_subject), getString(R.string.feature_suggestion_mail_message))))
                        .setNeutralButton(getString(R.string.cancel_button), (dialog, which) -> dialog.dismiss())
                        .show();
                return false;
            });

            Preference share_app = findPreference("share");
            assert share_app != null;
            share_app.setOnPreferenceClickListener(preference -> {

                Intent shareText = new Intent();

                shareText.setAction(Intent.ACTION_SEND);
                shareText.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_sharetext_message));
                shareText.setType("text/plain");

                Intent sendText = Intent.createChooser(shareText, null);

                startActivity(sendText);

                return false;
            });

            Preference feedback = findPreference("feedback");

            assert feedback != null;
            feedback.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.feedback_title))
                        .setMessage(getString(R.string.feedback_message))
                        .setIcon(R.drawable.ic_baseline_feedback_24)
                        .setPositiveButton(getString(R.string.telegram_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, telegram)))
                        .setNegativeButton(getString(R.string.email_button), (dialog, which) -> startActivity(composeEmail(email, getString(R.string.feedback_email_subject), getString(R.string.feedback_email_message))))
                        .setNeutralButton(getString(R.string.cancel_button), (dialog, which) -> dialog.dismiss())
                        .show();

                return false;

            });

            Preference app_permission = findPreference("permission");
            assert app_permission != null;
            app_permission.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.app_permission_title))
                        .setMessage(getString(R.string.app_permission_message))
                        .setIcon(R.drawable.ic_baseline_gpp_good_24)
                        .setPositiveButton("Okay", (dialog, which) -> dialog.dismiss())
                        .show();

                return false;
            });

            Preference privacy_policy = findPreference("policy");

            assert privacy_policy != null;
            privacy_policy.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.privacy_policy_title))
                        .setMessage(getString(R.string.privacy_policy_message))
                        .setIcon(R.drawable.ic_baseline_gpp_maybe_24)
                        .setPositiveButton("Okay", (dialog, which) -> dialog.dismiss())
                        .show();


                return false;

            });

            Preference license = findPreference("license");

            assert license != null;
            license.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.license_title))
                        .setMessage(getString(R.string.license_message))
                        .setIcon(R.drawable.ic_baseline_local_police_24)
                        .setPositiveButton("Okay", (dialog, which) -> dialog.dismiss())
                        .show();

                return false;
            });

            Preference wiki = findPreference("wiki");

            assert wiki != null;
            wiki.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.wiki_title))
                        .setMessage(getString(R.string.wiki_message))
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
                        .setNeutralButton(getString(R.string.cancel_button), (dialog, which) -> dialog.dismiss())
                        .show();
                return false;
            });
        }
    }
}