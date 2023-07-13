package com.lijukay.quotesAltDesign.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.service.InternetService;

import java.util.Locale;

public class Settings extends AppCompatActivity {

    private static SharedPreferences.Editor languageEditor, colorEditor;
    private static String languageValue, colorValue;

    @SuppressLint({"InflateParams", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences languagePreference = getSharedPreferences("Language", 0);
        SharedPreferences colorPreference = getSharedPreferences("Color", 0);
        languageEditor = languagePreference.edit();
        colorEditor = colorPreference.edit();
        languageValue = languagePreference.getString("language", "en");
        colorValue = colorPreference.getString("color", "defaultOrDynamic");

        Locale locale = new Locale(languageValue);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            setTheme(R.style.AppTheme);
        } else {
            switch (colorValue){
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

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        float scaleFactor = metrics.density;

        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        float smallestWidth = Math.min(widthDp, heightDp);

        setContentView(R.layout.settings_activity);

        boolean tablet = getResources().getBoolean(R.bool.isTablet);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);


        if (!tablet) ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.findViewById(R.id.settings).setPadding(0,0,0,insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        }); else ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.findViewById(R.id.settings).setPadding(0, insets.top, 0, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        MaterialToolbar materialToolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(materialToolbar);

        materialToolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (smallestWidth >= 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        else if (smallestWidth < 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent serviceIntent = new Intent(this, InternetService.class);
        startService(serviceIntent);
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {

        private final Uri QWOTABLE_REQUEST_GITHUB = Uri.parse("https://github.com/Lijukay/Qwotable/issues/new?assignees=&labels=qwotable+request&template=qwotable-request.md&title=Qwotable+request");
        private final Uri BUG_REPORT_GOOGLE_FORMS = Uri.parse("https://forms.gle/zXD69gpYWtghXAuF6");
        private final Uri QWOTABLE_REQUEST_GOOGLE_FORMS = Uri.parse("https://forms.gle/VKWUBDPPiiFkLcjRA");
        private final Uri BUG_REPORT_GITHUB = Uri.parse("https://github.com/Lijukay/Qwotable/issues/new?assignees=&labels=bug+report&template=bug_report.md&title=Bug+report");
        private final Uri FEATURE_REQUEST_GOOGLE_FORMS = Uri.parse("https://forms.gle/KZhEASuaQuBQtC9k6");
        private final Uri FEATURE_REQUEST_GITHUB = Uri.parse("https://github.com/Lijukay/Qwotable/issues/new?assignees=&labels=feature+request&template=feature_request.md&title=Feature+request");
        private final Uri FEEDBACK_GOOGLE_FORMS = Uri.parse("https://forms.gle/iCaaL1Nuck7u3bfE8");
        private final Uri FEEDBACK_GITHUB = Uri.parse("https://github.com/Lijukay/Qwotable/issues/new?assignees=&labels=feedback&template=feedback.md&title=Feedback");


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
                                        getString(R.string.color_default),
                                        getString(R.string.color_pink),
                                        getString(R.string.color_green),
                                        getString(R.string.color_red)
                                };
            } else {
                colors =
                        new CharSequence[]
                                {
                                        getString(R.string.color_dynamic),
                                        getString(R.string.color_pink),
                                        getString(R.string.color_green),
                                        getString(R.string.color_red)
                                };
            }
            int checkedColor = 0;

            int checkedItem = -1;


            Preference color = findPreference("colors");
            assert color != null;
            String colorSummary;
            switch (colorValue){
                case "pink":
                    checkedColor = 1;
                    colorSummary = getString(R.string.color_pink);
                    break;
                case "green":
                    checkedColor = 2;
                    colorSummary = getString(R.string.color_green);
                    break;
                case "red":
                    checkedColor = 3;
                    colorSummary = getString(R.string.color_red);
                    break;
                default:
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
                        colorSummary = getString(R.string.color_default);
                    } else {
                        colorSummary = getString(R.string.color_dynamic);
                    }
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
                                        colorEditor.putString("color", "defaultOrDynamic");
                                        break;
                                    case 1:
                                        colorEditor.putString("color", "pink");
                                        break;
                                    case 2:
                                        colorEditor.putString("color", "green");
                                        break;
                                    case 3:
                                        colorEditor.putString("color", "red");
                                        break;
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

            Preference qwrequest = findPreference("qwrequest");
            assert qwrequest != null;
            qwrequest.setOnPreferenceClickListener(preference -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.qwotable_request_dialog_title))
                        .setMessage(getString(R.string.qwotable_request_dialog_message))
                        .setIcon(R.drawable.ic_baseline_question_mark_24)
                        .setPositiveButton(getString(R.string.google_forms_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, QWOTABLE_REQUEST_GOOGLE_FORMS)))
                        .setNegativeButton(getString(R.string.github_button), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, QWOTABLE_REQUEST_GITHUB)))
                        .setNeutralButton(getString(R.string.neutral_button_text_cancel), (dialog, which) -> dialog.dismiss())
                        .show();


                return false;
            });


            switch (languageValue){
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
            language.setSummary(getString(R.string.language_preference_summary_language_supported));

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
                                    languageEditor.putString("Language", "en");
                                    break;
                                case 2:
                                    languageEditor.putString("Language", "fr");
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

            Preference bug_report = findPreference("reportBug");

            assert bug_report != null;
            bug_report.setOnPreferenceClickListener(preference -> {
                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.report_a_bug_dialog_title))
                        .setMessage(getString(R.string.report_a_bug_dialog_message))
                        .setIcon(R.drawable.ic_baseline_bug_report_24)
                        .setPositiveButton(getString(R.string.google_forms_button), (dialog, which) -> {
                            dialog.dismiss();
                            startActivity(new Intent(Intent.ACTION_VIEW, BUG_REPORT_GOOGLE_FORMS));
                        })
                        .setNegativeButton(getString(R.string.github_button), (dialog, which) -> {
                            dialog.dismiss();
                            startActivity(new Intent(Intent.ACTION_VIEW, BUG_REPORT_GITHUB));
                        })
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
                        .setPositiveButton(getString(R.string.google_forms_button), (dialog, which) -> {
                            dialog.dismiss();
                            startActivity(new Intent(Intent.ACTION_VIEW, FEATURE_REQUEST_GOOGLE_FORMS));
                        })
                        .setNegativeButton(getString(R.string.github_button), (dialog, which) -> {
                            dialog.dismiss();
                            startActivity(new Intent(Intent.ACTION_VIEW, FEATURE_REQUEST_GITHUB));
                        })
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
                        .setPositiveButton(getString(R.string.google_forms_button), (dialog, which) -> {
                            dialog.dismiss();
                            startActivity(new Intent(Intent.ACTION_VIEW, FEEDBACK_GOOGLE_FORMS));
                        })
                        .setNegativeButton(getString(R.string.github_button), (dialog, which) -> {
                            dialog.dismiss();
                            startActivity(new Intent(Intent.ACTION_VIEW, FEEDBACK_GITHUB));
                        })
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

                requireContext().startActivity(new Intent(requireContext(), License.class));

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
                            dialog.dismiss();
                            String url = "https://lijukay.gitbook.io/qwotable/";
                            CustomTabColorSchemeParams colorSchemeParams = new CustomTabColorSchemeParams.Builder().setToolbarColor(0x004D63).build();
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

            Preference special_thanks = findPreference("thanks");
            assert special_thanks != null;
            special_thanks.setOnPreferenceClickListener(preference -> {
                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.special_thanks))
                        .setIcon(R.drawable.thanks)
                        .setMessage(getString(R.string.special_message))
                        .setPositiveButton("Okay", (dialog, which) -> dialog.dismiss())
                        .show();
                return false;
            });
        }
    }
}