package com.lijukay.quotesAltDesign.activities;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import androidx.preference.PreferenceFragmentCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
    static String versionNameBeta, versionName, apkUrl, apkBeta, changelogMessage, changelogBeta, languageCode;
    static int versionC, versionA, versionB;
    static boolean internet;
    static SharedPreferences betaSP, language;
    static SharedPreferences.Editor betaEditor, languageEditor;
    private static RequestQueue mRequestQueueU;
    static boolean betaA = false, updateStatus = false;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        betaSP = getSharedPreferences("Beta", 0);
        language = getSharedPreferences("Language", 0);
        //------make the SharedPreference.Editor editing the SharedPreference with the variable-name "betaSP"------//
        betaEditor = betaSP.edit();
        languageEditor = language.edit();

        languageCode = language.getString("language", Locale.getDefault().getLanguage());
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());


        //------Set the contentView to the layout of settings_activity------//
        setContentView(R.layout.settings_activity);

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

        Log.e("Udpater", updateStatus+"");
        Log.e("Version", versionA+"");
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

        private Button negative, positive, neutral;
        private ConstraintLayout layout;
        private Uri telegram;
        private String email;


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            //int requestCode = 100; //TODO: Ask if plan works on older android devices or if it causes errors

            AlertDialog customDialog;
            View alertCustomDialog = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bg, null);
            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(requireContext());
            alertDialog2.setView(alertCustomDialog);
            customDialog = alertDialog2.create();
            customDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            customDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            positive = alertCustomDialog.findViewById(R.id.positive_button);
            negative = alertCustomDialog.findViewById(R.id.negative_button);
            neutral = alertCustomDialog.findViewById(R.id.neutral_button);
            layout = alertCustomDialog.findViewById(R.id.buttons);

            neutral.setOnClickListener(view -> customDialog.dismiss());

            telegram = Uri.parse("https://t.me/Lijukay");

            email = "luca.krumminga@gmail.com";


            parseJSONVersion();


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
            qwrequest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {

                    showCustomDialog();

                    return false;
                }

                private void showCustomDialog() {
                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    LinearLayout languageandtheme = alertCustomDialog.findViewById(R.id.themeandlanguage);

                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.qwotable);

                    title.setText(getString(R.string.qwrequest_title));

                    message.setVisibility(View.VISIBLE);
                    message.setText(getString(R.string.qwrequest_message));

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    messageCard.setLayoutParams(params);

                    layout.setVisibility(View.VISIBLE);
                    negative.setVisibility(View.VISIBLE);

                    languageandtheme.setVisibility(View.GONE);

                    positive.setText(getString(R.string.email_button));
                    positive.setOnClickListener(view -> startActivity(composeEmail(email, getString(R.string.qwrequest_email_subject), getString(R.string.qwrequest_email_message))));
                    negative.setText(getString(R.string.telegram_button));
                    negative.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, telegram)));

                    customDialog.show();
                }
            });


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

            language.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    showCustomDialog();
                    return false;
                }

                private void showCustomDialog() {
                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    LinearLayout themeandlanguage = alertCustomDialog.findViewById(R.id.themeandlanguage);


                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.language);

                    title.setText(getString(R.string.language_title));

                    message.setVisibility(View.GONE);

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    messageCard.setLayoutParams(params);

                    themeandlanguage.setVisibility(View.VISIBLE);

                    layout.setVisibility(View.GONE);

                    alertCustomDialog.findViewById(R.id.german_language).setOnClickListener(v -> {
                        languageEditor.putString("language", "de").apply();
                        requireActivity().startActivity(new Intent(requireActivity(), MainActivity.class));
                        requireActivity().overridePendingTransition(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out);
                        requireActivity().finishAffinity();
                    });

                    alertCustomDialog.findViewById(R.id.english_language).setOnClickListener(v ->{
                        languageEditor.putString("language", "en").apply();
                        requireActivity().startActivity(new Intent(requireActivity(), MainActivity.class));
                        requireActivity().overridePendingTransition(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out);
                        requireActivity().finishAffinity();
                    });

                    alertCustomDialog.findViewById(R.id.french_language).setOnClickListener(v -> {
                        languageEditor.putString("language", "fr").apply();
                        requireActivity().startActivity(new Intent(requireActivity(), MainActivity.class));
                        requireActivity().overridePendingTransition(rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out);
                        requireActivity().finishAffinity();
                    });

                    customDialog.show();
                }
            });

            Preference updater = findPreference("updateCheck");

            assert updater != null;
            updater.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {

                    if (!internet) {
                        Toast.makeText(requireContext(), getString(R.string.cant_check_for_updates), Toast.LENGTH_SHORT).show();
                    } else  if (!updateStatus && !betaA && betaSP.getBoolean("beta", false) || !updateStatus && !betaSP.getBoolean("beta", false)){
                        Toast.makeText(requireContext(), getString(R.string.no_update_available), Toast.LENGTH_SHORT).show();
                    } else if (updateStatus){
                        showCustomDialog(false);
                    } else if (betaA && betaSP.getBoolean("beta", false)){
                        showCustomDialog(true);
                    }

                    return false;

                }

                private void showCustomDialog(boolean beta) {
                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    LinearLayout themeandlanguage = alertCustomDialog.findViewById(R.id.themeandlanguage);

                    themeandlanguage.setVisibility(View.GONE);

                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.app_update);

                    title.setText(getString(R.string.update_title));

                    message.setVisibility(View.VISIBLE);


                    if (betaSP.getBoolean("beta", false) && beta) {
                        message.setText(changelogBeta);
                    } else {
                        message.setText(changelogMessage);
                    }

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = 400;

                    messageCard.setLayoutParams(params);

                    layout.setVisibility(View.VISIBLE);

                    negative.setVisibility(View.GONE);

                    positive.setText(getString(R.string.update_button));
                    positive.setOnClickListener(view -> {
                        //int check = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (/*check == PackageManager.PERMISSION_GRANTED &&*/ beta && betaSP.getBoolean("beta", false)) {
                            InstallUpdate(requireActivity(), apkBeta, versionNameBeta);
                        } else /*if (check == PackageManager.PERMISSION_GRANTED)*/ {
                            InstallUpdate(requireActivity(), apkUrl, versionName);
                        } /*else {
                            customDialog.dismiss();
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                        }*/
                    });

                    customDialog.show();
                }
            });


            //TODO: OPTIMIZE EVERYTHING BELOW HERE!

            Preference bug_report = findPreference("reportBug");

            assert bug_report != null;
            bug_report.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {

                    showCustomDialog();

                    return false;

                }

                private void showCustomDialog() {

                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);

                    LinearLayout themeandlanguage = alertCustomDialog.findViewById(R.id.themeandlanguage);
                    themeandlanguage.setVisibility(View.GONE);

                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.bug_report);

                    title.setText(getString(R.string.bug_report_title));

                    message.setVisibility(View.VISIBLE);
                    message.setText(getString(R.string.bug_report_message));


                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                    messageCard.setLayoutParams(params);

                    layout.setVisibility(View.VISIBLE);

                    positive.setText(getString(R.string.email_button));
                    positive.setOnClickListener(view -> startActivity(composeEmail(email, getString(R.string.bug_report_mail_subject), getString(R.string.bug_report_mail_message))));

                    negative.setVisibility(View.VISIBLE);
                    negative.setText(getString(R.string.telegram_button));
                    negative.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, telegram)));

                    customDialog.show();
                }


            });


            Preference feature_suggestion = findPreference("feature");
            assert feature_suggestion != null;
            feature_suggestion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    showCustomDialog();
                    return false;
                }

                private void showCustomDialog() {
                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);

                    LinearLayout themeandlanguage = alertCustomDialog.findViewById(R.id.themeandlanguage);
                    themeandlanguage.setVisibility(View.GONE);

                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.feature_suggestion);

                    title.setText(getString(R.string.feature_suggestion_title));

                    message.setVisibility(View.VISIBLE);
                    message.setText(getString(R.string.feature_suggestion_message));

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                    messageCard.setLayoutParams(params);

                    layout.setVisibility(View.VISIBLE);

                    positive.setText(getString(R.string.email_button));
                    positive.setOnClickListener(view -> startActivity(composeEmail(email, getString(R.string.feature_suggestion_mail_subject), getString(R.string.feature_suggestion_mail_message))));

                    negative.setText(getString(R.string.telegram_button));
                    negative.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, telegram)));

                    customDialog.show();
                }
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
            feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {

                    showCustomDialog();

                    return false;

                }

                private void showCustomDialog() {
                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);

                    LinearLayout themeandlanguage = alertCustomDialog.findViewById(R.id.themeandlanguage);
                    themeandlanguage.setVisibility(View.GONE);

                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.feedback);

                    title.setText(getString(R.string.feedback_title));

                    message.setVisibility(View.VISIBLE);
                    message.setText(getString(R.string.feedback_message));


                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    messageCard.setLayoutParams(params);

                    layout.setVisibility(View.VISIBLE);

                    positive.setText(getString(R.string.email_button));
                    positive.setOnClickListener(view -> startActivity(composeEmail(email, getString(R.string.feedback_email_subject), getString(R.string.feedback_email_message))));

                    negative.setText(getString(R.string.telegram_button));
                    negative.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, telegram)));

                    customDialog.show();
                }
            });

            Preference app_permission = findPreference("permission");
            assert app_permission != null;
            app_permission.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    showCustomDialog();
                    return false;
                }

                private void showCustomDialog() {

                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);

                    LinearLayout themeandlanguage = alertCustomDialog.findViewById(R.id.themeandlanguage);
                    themeandlanguage.setVisibility(View.GONE);

                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.permissions);

                    title.setText(getString(R.string.app_permission_title));

                    message.setVisibility(View.VISIBLE);
                    message.setText(getString(R.string.app_permission_message));

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = 600;

                    messageCard.setLayoutParams(params);

                    layout.setVisibility(View.GONE);

                    customDialog.show();
                }
            });

            Preference privacy_policy = findPreference("policy");

            assert privacy_policy != null;
            privacy_policy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {

                    showCustomDialog();

                    return false;

                }

                private void showCustomDialog() {

                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);

                    LinearLayout themeandlanguage = alertCustomDialog.findViewById(R.id.themeandlanguage);
                    themeandlanguage.setVisibility(View.GONE);

                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.privacy_policy);

                    title.setText(getString(R.string.privacy_policy_title));

                    message.setVisibility(View.VISIBLE);
                    message.setText(getString(R.string.privacy_policy_message));


                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = 600;

                    messageCard.setLayoutParams(params);

                    layout.setVisibility(View.GONE);

                    customDialog.show();
                }
            });

            Preference license = findPreference("license");

            assert license != null;
            license.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {

                    showCustomDialog();

                    return false;
                }

                private void showCustomDialog() {

                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    LinearLayout themeandlanguage = alertCustomDialog.findViewById(R.id.themeandlanguage);

                    themeandlanguage.setVisibility(View.GONE);

                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.license);

                    title.setText(getString(R.string.license_title));

                    message.setVisibility(View.VISIBLE);
                    message.setText(getString(R.string.license_message));

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                    messageCard.setLayoutParams(params);

                    layout.setVisibility(View.GONE);

                    customDialog.show();
                }
            });

            Preference wiki = findPreference("wiki");

            assert wiki != null;
            wiki.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {

                    showCustomDialog();

                    return false;
                }

                private void showCustomDialog() {

                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);

                    LinearLayout themeandlanguage = alertCustomDialog.findViewById(R.id.themeandlanguage);
                    themeandlanguage.setVisibility(View.GONE);

                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.wiki);

                    title.setText(getString(R.string.wiki_title));

                    message.setVisibility(View.VISIBLE);
                    message.setText(getString(R.string.wiki_message));

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                    messageCard.setLayoutParams(params);

                    layout.setVisibility(View.VISIBLE);

                    negative.setVisibility(View.GONE);

                    positive.setText(getText(R.string.wiki_button));
                    positive.setOnClickListener(view -> {
                        String url = "https://lijukay.gitbook.io/qwotable/";

                        CustomTabColorSchemeParams colorSchemeParams = new CustomTabColorSchemeParams.Builder().setToolbarColor(getResources().getColor(R.color.md_theme_dark_onPrimary, requireActivity().getTheme())).build();

                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        builder.setCloseButtonPosition(CustomTabsIntent.CLOSE_BUTTON_POSITION_DEFAULT);
                        builder.setStartAnimations(requireContext(), rikka.core.R.anim.fade_in, rikka.core.R.anim.fade_out);
                        builder.setExitAnimations(requireContext(), rikka.core.R.anim.fade_out, rikka.core.R.anim.fade_in);
                        builder.setDefaultColorSchemeParams(colorSchemeParams);
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
                    });

                    customDialog.show();
                }


            });

        }


        private void showPermissionDialog() {

            AlertDialog customDialog;

            View alertCustomDialog = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bg, null);

            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(requireContext());
            alertDialog2.setView(alertCustomDialog);

            customDialog = alertDialog2.create();
            //customDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            customDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            positive = alertCustomDialog.findViewById(R.id.positive_button);

            negative = alertCustomDialog.findViewById(R.id.negative_button);

            neutral = alertCustomDialog.findViewById(R.id.neutral_button);

            layout = alertCustomDialog.findViewById(R.id.buttons);

            neutral.setOnClickListener(view -> customDialog.dismiss());

            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);

            assert lottieAnimationView != null;
            lottieAnimationView.setAnimation(R.raw.permissions);

            TextView title = alertCustomDialog.findViewById(R.id.custom_title);
            title.setText(getString(R.string.permission_required_title));

            TextView message = alertCustomDialog.findViewById(R.id.message_text);
            message.setText(getString(R.string.permission_required_message));

            CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
            params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

            messageCard.setLayoutParams(params);

            layout.setVisibility(View.VISIBLE);

            positive.setText(getString(R.string.grant_permission_button));
            positive.setOnClickListener(view -> {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);

                intent.setData(uri);

                startActivity(intent);

                customDialog.dismiss();

                Toast.makeText(requireActivity(), "Go to \"Permission\" > \"Storage\" > \"Allow\"", Toast.LENGTH_SHORT).show();
            });

            negative.setVisibility(View.GONE);

            LinearLayout themeandlanguage = alertCustomDialog.findViewById(R.id.themeandlanguage);
            themeandlanguage.setVisibility(View.GONE);

            customDialog.show();
        }

        @SuppressWarnings("deprecation")
        @SuppressLint({"MissingSuperCall"})
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            int PERMISSION_REQUEST_CODE_WRITE_EXTERNAL = 100;

            if (requestCode == PERMISSION_REQUEST_CODE_WRITE_EXTERNAL && (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
                showPermissionDialog();
            }

            if (requestCode == PERMISSION_REQUEST_CODE_WRITE_EXTERNAL && (grantResults.length <= 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                InstallUpdate(requireActivity(), apkUrl, versionName);
            }
        }
    }
}