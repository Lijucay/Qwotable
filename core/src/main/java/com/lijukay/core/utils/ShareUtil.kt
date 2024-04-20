package com.lijukay.core.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.lijukay.core.R

class ShareUtil {
    companion object {
        fun String.shareExternally(context: Context) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.qwotable))
            intent.putExtra(Intent.EXTRA_TEXT, this)
            context.startActivity(
                Intent.createChooser(
                    intent,
                    context.getString(R.string.share_using)
                )
            )
        }

        @Throws(Exception::class)
        fun shareBitmap(context: Context, bitmap: Bitmap) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "${context.getString(R.string.qwotable)}_${System.currentTimeMillis()}"
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val imageUri: Uri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw Exception("Failed to create new MediaStore record.")

            resolver.openOutputStream(imageUri).use { outputStream ->
                if (outputStream == null) {
                    throw Exception("Failed to open output stream.")
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, imageUri)
            }

            context.startActivity(
                Intent.createChooser(
                    intent,
                    context.getString(R.string.share_using)
                )
            )
        }
    }
}