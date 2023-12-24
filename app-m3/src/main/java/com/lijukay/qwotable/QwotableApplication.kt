package com.lijukay.qwotable

import android.app.Application
import com.google.android.material.color.DynamicColors

class QwotableApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}