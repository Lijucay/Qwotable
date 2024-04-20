package com.lijukay.core.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class ClipboardUtil {
    companion object {
        fun String.copyToClipboard(context: Context) {
            val clipboardManager: ClipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("Qwotable", this)
            clipboardManager.setPrimaryClip(clip)
        }
    }
}