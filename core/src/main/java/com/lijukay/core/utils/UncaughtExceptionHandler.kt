/*
* Copyright (C) 2024 Lijucay (Luca)
*
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation, either version 3 of the License, or
*   (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*   along with this program.  If not, see <https://www.gnu.org/licenses/>
* */

package com.lijukay.core.utils

import android.content.Context
import android.content.Intent
import android.os.Build
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
        e.printStackTrace()

        if (logs != null) {
            try {
                val outputStream = context.openFileOutput("logs.txt", Context.MODE_PRIVATE)
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