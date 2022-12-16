package com.lijukay.quotesAltDesign.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
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
import com.lijukay.quotesAltDesign.service.InternetService;
import com.lijukay.quotesAltDesign.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Settings extends AppCompatActivity {
    static Intent starterIntent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static RequestQueue mRequestQueueU;
    static String versionName;
    static String apkUrl;
    static String changelogMessage;
    static String updateStatus;
    static int versionC;
    static int versionA;
    static boolean internet;
    public static final String BroadcastStringForAction = "checkInternet";
    private IntentFilter mIntentFilter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
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

        starterIntent = getIntent();

        mIntentFilter = new IntentFilter();
        //------Action of this IntentFilter: Checking the internet------//
        mIntentFilter.addAction(BroadcastStringForAction);
        //------Referring to the class where the service is written down and starting the service------//
        Intent serviceIntent = new Intent(this, InternetService.class);
        startService(serviceIntent);
        //------Checking if the Application is online------//
        internet = isOnline(getApplicationContext());
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements com.lijukay.quotesAltDesign.activities.SettingsFragment {

        private Button negative;
        private Button positive;
        private Button neutral;
        private ConstraintLayout layout;
        private Uri telegram;
        private String email;


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            int requestCode = 100;




            AlertDialog customDialog;
            View alertCustomDialog = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bg, null);
            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(requireContext());
            alertDialog2.setView(alertCustomDialog);
            customDialog = alertDialog2.create();
            //customDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            customDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            AlertDialog customWebDialog;
            View alertCustomWebDialog = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_website, null);
            AlertDialog.Builder alertDialogWeb = new AlertDialog.Builder(requireContext());
            alertDialogWeb.setView(alertCustomWebDialog);
            customWebDialog = alertDialogWeb.create();
            customWebDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            positive = alertCustomDialog.findViewById(R.id.positive_button);
            negative = alertCustomDialog.findViewById(R.id.negative_button);
            neutral = alertCustomDialog.findViewById(R.id.neutral_button);
            layout = alertCustomDialog.findViewById(R.id.buttons);
            neutral.setOnClickListener(view -> customDialog.dismiss());
            telegram = Uri.parse("https://t.me/Lijukay");
            email = "luca.krumminga@gmail.com";


            parseJSONVersion();


            Preference language = findPreference("language");
            Preference updater = findPreference("updateCheck");
            Preference bug_report = findPreference("reportBug");
            Preference feature_suggestion = findPreference("feature");
            Preference share_app = findPreference("share");
            Preference feedback = findPreference("feedback");
            Preference app_permission = findPreference("permission");
            Preference privacy_policy = findPreference("policy");
            Preference license = findPreference("license");
            Preference wiki = findPreference("wiki");
            Preference qwrequest = findPreference("qwrequest");


            qwrequest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    showCustomDialog();
                    return false;
                }
                private void showCustomDialog() {
                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.qwotable);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    title.setText(getString(R.string.qwrequest_title));
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText(getString(R.string.qwrequest_message));

                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    messageCard.setLayoutParams(params);
                    layout.setVisibility(View.VISIBLE);

                    positive.setText(getString(R.string.email_button));
                    positive.setOnClickListener(view -> startActivity(composeEmail(email, getString(R.string.qwrequest_email_subject), getString(R.string.qwrequest_email_message))));
                    negative.setText(getString(R.string.telegram_button));
                    negative.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, telegram)));


                    customDialog.show();
                }
            });



            assert language != null;
            language.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    showCustomDialog();
                    return false;
                }

                private void showCustomDialog() {
                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.language);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    title.setText(getString(R.string.language_title));
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText(getString(R.string.language_message_temp)); //TODO:ADD POSSIBILITY TO SELECT LANGUAGE + CHANGE THE MESSAGE

                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    messageCard.setLayoutParams(params);
                    layout.setVisibility(View.GONE);
                    customDialog.show();
                }
            });

            assert updater != null;
            updater.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {

                    if(!internet){
                        Toast.makeText(requireContext(), getString(R.string.cant_check_for_updates), Toast.LENGTH_SHORT).show();
                    } else if (updateStatus.equals("No update")){
                        Toast.makeText(requireContext(), getString(R.string.no_update_available), Toast.LENGTH_SHORT).show();
                    } else {
                        showCustomDialog();
                    }
                    return false;
                }

                private void showCustomDialog() {
                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.app_update);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    title.setText(getString(R.string.update_title));
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText(changelogMessage);
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = 400;
                    messageCard.setLayoutParams(params);
                    layout.setVisibility(View.VISIBLE);
                    negative.setVisibility(View.GONE);
                    positive.setText(getString(R.string.update_button));
                    positive.setOnClickListener(view -> {
                        int check = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (check == PackageManager.PERMISSION_GRANTED) {
                            InstallUpdate(requireActivity(), apkUrl, versionName);
                        } else {
                            customDialog.dismiss();
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                        }
                    });

                    customDialog.show();
                }
            });

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
                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.bug_report);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    title.setText(getString(R.string.bug_report_title));
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText(getString(R.string.bug_report_message));
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    messageCard.setLayoutParams(params);
                    layout.setVisibility(View.VISIBLE);
                    positive.setText(getString(R.string.email_button));
                    positive.setOnClickListener(view -> startActivity(composeEmail(email, getString(R.string.bug_report_mail_subject), getString(R.string.bug_report_mail_message))));
                    negative.setText(getString(R.string.telegram_button));
                    negative.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, telegram)));

                    customDialog.show();
                }


            });

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
                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.feature_suggestion);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    title.setText(getString(R.string.feature_suggestion_title));
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText(getString(R.string.feature_suggestion_message));
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
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
                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.feedback);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    title.setText(getString(R.string.feedback_title));
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText(getString(R.string.feedback_message));
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
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
                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.permissions);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    title.setText(getString(R.string.app_permission_title));
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText(getString(R.string.app_permission_message));
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = 600;
                    messageCard.setLayoutParams(params);
                    layout.setVisibility(View.GONE);

                    customDialog.show();
                }
            });

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
                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.privacy_policy);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    title.setText(getString(R.string.privacy_policy_title));
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText(getString(R.string.privacy_policy_message));
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = 600;
                    messageCard.setLayoutParams(params);
                    layout.setVisibility(View.GONE);

                    customDialog.show();
                }
            });

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
                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.license);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    title.setText(getString(R.string.license_title));
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText(getString(R.string.license_message));
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    messageCard.setLayoutParams(params);
                    layout.setVisibility(View.GONE);

                    customDialog.show();
                }
            });

            assert wiki != null;
            wiki.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    showCustomDialog();
                    return false;
                }
                @SuppressLint("SetJavaScriptEnabled")
                private void showCustomDialog() {
                    customWebDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView title = alertCustomWebDialog.findViewById(R.id.custom_title_wiki);
                    title.setText("Qwotable Wiki");
                    ImageView close = alertCustomWebDialog.findViewById(R.id.close);


                    WebView webView = alertCustomWebDialog.findViewById(R.id.website);
                    webView.loadUrl("https://lijukay.gitbook.io/qwotable/");
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setWebViewClient(new WebViewClient());
                    webView.getSettings().setAppCacheEnabled(false);


                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            webView.clearCache(true);
                            customWebDialog.dismiss();
                        }
                    });

                    customWebDialog.show();
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

    private static void parseJSONVersion() {
        String urlU = "https://lijukay.github.io/PrUp/prUp.json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlU, null,
                jsonObject -> {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("Qwotable");

                        for (int a = 0; a < jsonArray.length(); a++) {

                            JSONObject v = jsonArray.getJSONObject(a);

                            versionC = BuildConfig.VERSION_CODE;
                            versionA = v.getInt("versionsCode");
                            apkUrl = v.getString("apkUrl");
                            changelogMessage = v.getString("changelog");
                            versionName = v.getString("versionsName");
                        }

                        if (versionA > versionC) {
                            updateStatus = "Update available";
                        } else {
                            updateStatus = "No update";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);

        mRequestQueueU.add(jsonObjectRequest);


    }


    public final BroadcastReceiver InternetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BroadcastStringForAction)){
                internet = intent.getStringExtra("online_status").equals("true");
            }
        }
    };

    public boolean isOnline(Context c){
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
}