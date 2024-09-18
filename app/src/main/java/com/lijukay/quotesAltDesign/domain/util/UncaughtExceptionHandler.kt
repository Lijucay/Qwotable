package com.lijukay.quotesAltDesign.domain.util

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.os.Process.killProcess
import android.os.Process.myPid
import com.lijukay.quotesAltDesign.BuildConfig
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class UncaughtExceptionHandler(
    private val context: Context,
    private val logIntent: Intent,
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        with(context) {
            val logs = e.message
            val logWriter = StringWriter()

            val versionCode = BuildConfig.VERSION_CODE
            val versionName = BuildConfig.VERSION_NAME

            e.printStackTrace(PrintWriter(logWriter))
            e.printStackTrace()

            if (logs != null) {
                try {
                    val outputStream = openFileOutput("logs.txt", Context.MODE_PRIVATE)
                    outputStream.write(
                        (
                                "------Message------\n\n" +
                                        "$e\n\n------Logs------\n\n" +
                                        "${logWriter.toString().replace(",", "\n")}\n\n" +
                                        "------App information------\n\n" +
                                        "Version Code: $versionCode\n" +
                                        "Version Name: $versionName" +
                                        "\n\n------Device information------\n\n" +
                                        "Android version: ${Build.VERSION.RELEASE}\n" +
                                        "Android SDK Integer: ${Build.VERSION.SDK_INT}\n" +
                                        "Device Manufacturer: ${Build.MANUFACTURER}\n" +
                                        "Device Brand: ${Build.BRAND}\n" +
                                        "Device Model: ${Build.MODEL}"
                                ).toByteArray()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                logIntent.apply {
                    addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_CLEAR_TASK)
                    putExtra("filePath", filesDir.toString() + File.separator + "logs.txt")
                    putExtra("logs", logs)
                    putExtra("logs_detailed", logWriter.toString())

                    startActivity(logIntent)
                    killProcess(myPid())
                    exitProcess(1)
                }
            }
        }
    }
}