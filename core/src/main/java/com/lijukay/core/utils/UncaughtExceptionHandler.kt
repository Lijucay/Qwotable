package com.lijukay.core.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import com.lijukay.core.BuildConfig
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class UncaughtExceptionHandler(
    private val context: Context,
    private val logIntent: Intent,
    private val versionCode: Int,
    private val versionName: String,
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        val logs = e.message
        val logWriter = StringWriter()

        e.printStackTrace(PrintWriter(logWriter))

        if (logs != null) {
            try {
                val outputStream = context.openFileOutput("logs.txt", Context.MODE_PRIVATE)
                outputStream.write(
                    (
                        "------Message------\n\n" +
                        "$e------Logs------\n\n" +
                        "${logWriter.toString().replace(",", "\n")}\n\n" +
                        "------App information------\n\n" +
                        "Version Code: $versionCode\n" +
                                "Version Name: $versionName" +
                                "------Device information------\n\n" +
                                "Android version: ${Build.VERSION.RELEASE}\n" +
                                "Android SDK Integer: ${Build.VERSION.SDK_INT}\n" +
                                "Device Manufacturer: ${Build.MANUFACTURER}" +
                                "Device Brand: ${Build.BRAND}\n" +
                                "Device Model: ${Build.MODEL}"
                    ).toByteArray()
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        logIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        logIntent.putExtra("filePath", context.filesDir.toString() + File.separator + "logs.txt")
        logIntent.putExtra("logs", logs)
        logIntent.putExtra("logs_detailed", logWriter.toString())

        context.startActivity(logIntent)
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(1)
    }
}