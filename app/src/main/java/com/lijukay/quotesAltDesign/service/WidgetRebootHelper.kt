package com.lijukay.quotesAltDesign.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lijukay.quotesAltDesign.QwotableWidget

class WidgetRebootHelper : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            QwotableWidget.updateOnReboot(context)
        }
    }
}