package com.lijukay.quotesAltDesign.domain.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardUtil {
    fun copyToClipboard(
        context: Context,
        qwotable: String,
        author: String? = null,
        source: String? = null
    ) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val textToCopy = StringBuilder(qwotable)
        if (author != null) {
            textToCopy.appendLine("\n$author")
        }
        if (source != null) {
            textToCopy.append("\n$source")
        }
        val result = textToCopy.toString()

        val clipData = ClipData.newPlainText(
            "Qwotable",
            result
        )

        clipboardManager.setPrimaryClip(clipData)
    }

    fun String.copyToClipboard(context: Context) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clipData = ClipData.newPlainText(
            "Qwotable",
            this
        )

        clipboardManager.setPrimaryClip(clipData)
    }
}