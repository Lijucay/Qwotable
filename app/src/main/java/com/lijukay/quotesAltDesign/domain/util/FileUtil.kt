package com.lijukay.quotesAltDesign.domain.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lijukay.quotesAltDesign.data.local.model.OwnQwotable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStream

object FileUtil {
    fun String.saveFile(context: Context, fileName: String, mimeType: String) {
        val os: OutputStream? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
                )
                val extVolumeUri = MediaStore.Files.getContentUri("external")
                val fileUri = context.contentResolver.insert(extVolumeUri, values)
                fileUri?.let { context.contentResolver.openOutputStream(it) }
            } else {
                val path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString()

                val file = File(path, fileName)
                FileOutputStream(file)
            }

        val bytes = this.toByteArray()
        os?.write(bytes)
        os?.close()
    }

    fun saveFileToDownloads(filesDir: String?): Boolean {
        val logFile = filesDir?.let { File(it) }

        if (logFile?.exists() == true) {
            val fis = FileInputStream(logFile)
            val fos = FileOutputStream(
                "${Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )}/qwotable_logs_${System.currentTimeMillis()}.txt"
            )

            val buffer = ByteArray(1024)
            var length: Int

            while (fis.read(buffer).also { length = it } > 0) {
                fos.write(buffer, 0, length)
            }

            fos.flush()
            fos.close()
            fis.close()

            return true
        } else {
            return false
        }
    }

    fun createBackupFile(
        context: Context,
        ownQwotables: List<OwnQwotable>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val gson = Gson()
            val jsonString = gson.toJson(ownQwotables)

            jsonString.saveFile(context, "Backup_Qwotables.qwote", "application/octet-stream")
        }
    }

    suspend fun readFromBackupFile(
        context: Context,
        uri: Uri
    ): List<OwnQwotable>? {
        return withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)

                inputStream?.let {
                    val bufferedReader = BufferedReader(InputStreamReader(it))
                    val jsonText = bufferedReader.readText()

                    val gson = Gson()
                    val listType = object : TypeToken<List<OwnQwotable>>() {}.type
                    gson.fromJson<List<OwnQwotable>>(jsonText, listType)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}