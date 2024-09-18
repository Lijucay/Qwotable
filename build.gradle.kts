// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.kotlin.plugin.compose) apply false
    alias(libs.plugins.aboutlibraries.plugin) apply false
}