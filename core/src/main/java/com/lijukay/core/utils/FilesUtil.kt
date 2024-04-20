package com.lijukay.core.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class FilesUtil {
    companion object {
        @Throws(IOException::class)
        fun String.saveFile(context: Context, fileName: String) {
            val os: OutputStream? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues()
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    values.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS
                    )
                    val extVolumeUri: Uri = MediaStore.Files.getContentUri("external")
                    val fileUri: Uri? = context.contentResolver.insert(extVolumeUri, values)
                    context.contentResolver.openOutputStream(fileUri!!)
                } else {
                    val path =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .toString()
                    val file = File(path, "$fileName.txt")
                    FileOutputStream(file)
                }

            val bytes = this.toByteArray()
            os?.write(bytes)
            os?.close()
        }

        @Throws(IOException::class)
        fun saveFileToDownloads(filesDir: String?): Boolean {
            val logFile = filesDir?.let { File(it) }
            if (logFile?.exists() == true) {
                val fis = FileInputStream(logFile)
                val fos =
                    FileOutputStream("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/logs_${System.currentTimeMillis()}.txt")

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
    }

}