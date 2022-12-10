package com.lijukay.quotesAltDesign;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.Button;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
            Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show();
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

    public static class SettingsFragment extends PreferenceFragmentCompat {

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
                    title.setText("Language");
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText("Not available yet");

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
                        Toast.makeText(requireContext(), "Can't check for updates", Toast.LENGTH_SHORT).show();
                    } else if (updateStatus.equals("No update")){
                        Toast.makeText(requireContext(), "No updates available", Toast.LENGTH_SHORT).show();
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
                    title.setText("Updater");
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText(changelogMessage);
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = 400;
                    messageCard.setLayoutParams(params);
                    layout.setVisibility(View.VISIBLE);
                    negative.setVisibility(View.GONE);
                    positive.setText("Update");
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
                    title.setText("Bug report");
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText("If you found a bug, I am really sorry. I will do my best to make this app better. In the meanwhile, you can decide how to reach me to report the bug you found.\nDo you want to use Telegram or Email?");
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    messageCard.setLayoutParams(params);
                    layout.setVisibility(View.VISIBLE);
                    positive.setText("Email");
                    positive.setOnClickListener(view -> startActivity(composeEmail("luca.krumminga@gmail.com", "Bug has been found", "Hello,\n\nUnfortunately I found a bug:")));
                    negative.setText("Telegram");
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
                    title.setText("Feature suggestion");
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText("Have a suggestion?");
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    messageCard.setLayoutParams(params);
                    layout.setVisibility(View.VISIBLE);
                    positive.setText("Email");
                    positive.setOnClickListener(view -> startActivity(composeEmail(email, "Suggestion of a feature", "Hello,\n\nI have a very nice idea. Hear me out:")));
                    negative.setText("Telegram");
                    negative.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, telegram)));


                    customDialog.show();
                }
            });

            assert share_app != null;
            share_app.setOnPreferenceClickListener(preference -> {
                Intent shareText = new Intent();
                shareText.setAction(Intent.ACTION_SEND);
                shareText.putExtra(Intent.EXTRA_TEXT, "https://github.com/Lijukay/Qwotable");
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
                    title.setText("Feedback");
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText("Do you have Feedback for my application? You can easily write your opinion to me by choosing Telegram or Email. Please don't just write \"I (do not) like your app.\"\nBe creative. Tell me what you (do not) like.");
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    messageCard.setLayoutParams(params);
                    layout.setVisibility(View.VISIBLE);
                    positive.setText("Email");
                    positive.setOnClickListener(view -> startActivity(composeEmail(email, "About your app", "Hello,\n\nListen to the melodie of my feedback:")));
                    negative.setText("Telegram");
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
                    title.setText("App's permission");
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText("This app uses some permissions. This page will explain to you why my app needs these permissions.\n\nRead and write storage: The Quotes app needs to be able to read and write to the storage in order to save and install app updates on the device. No data is written to storage.\n\nFull Internet Access: This permission allows my app to get the quotes from my file on the Internet. No data is collected from me.\n\nInstall unknown packages: This permission allows my app to install the update.");
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
                    title.setText("Privacy Policy");
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText("Quotes is a free Android application, made by me, Luca. Quotes does not collect any data and never will. Every interaction with me is selectable. I am offering the possibility of sending me mails, which includes bugs, features and/or feedback. When the conversation with the user has ended, I will delete the contact data and the conversation due to the security of data.\n\nI may change the Privacy Policy. Every change is effective immediately. An information of the change of the Privacy Policy can be found on the changelog\n\nEffective September 3, 2022");
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
                    title.setText("License");
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText("For my application, I used some things that are not mine: Lottie Animations\nHowever, because the files are all signed with the Lottie Simple License I have the chance to not mention the creator which. I normally don't like that but I am too lazy to search every creator now. I will add it in the future.");
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
                private void showCustomDialog() {
                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    LottieAnimationView lottieAnimationView = alertCustomDialog.findViewById(R.id.lottie_file);
                    assert lottieAnimationView != null;
                    lottieAnimationView.setAnimation(R.raw.wiki);
                    TextView title = alertCustomDialog.findViewById(R.id.custom_title);
                    title.setText("Wiki");
                    TextView message = alertCustomDialog.findViewById(R.id.message_text);
                    message.setText("Hey, cool that you are here. Qwotable has a Wiki-Website. How about having a look?");
                    CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    messageCard.setLayoutParams(params);
                    layout.setVisibility(View.VISIBLE);
                    positive.setText("To the Wiki");
                    positive.setOnClickListener(view -> {
                        Uri wiki1 = Uri.parse("https://lijukay.gitbook.io/qwotable/");
                        startActivity(new Intent(Intent.ACTION_VIEW, wiki1));
                    });
                    negative.setVisibility(View.GONE);

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
            lottieAnimationView.setAnimation(R.raw.wiki);
            TextView title = alertCustomDialog.findViewById(R.id.custom_title);
            title.setText("Permission required");
            TextView message = alertCustomDialog.findViewById(R.id.message_text);
            message.setText("In order to update this application, this app needs the permission to read and write storage");
            CardView messageCard = alertCustomDialog.findViewById(R.id.message_card);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) messageCard.getLayoutParams();
            params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            messageCard.setLayoutParams(params);
            layout.setVisibility(View.VISIBLE);
            positive.setText("Grant permission");
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