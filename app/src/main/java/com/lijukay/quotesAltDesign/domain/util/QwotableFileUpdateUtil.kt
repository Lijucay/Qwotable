package com.lijukay.quotesAltDesign.domain.util

import android.content.Context
import com.lijukay.quotesAltDesign.domain.util.apis.QwotableAPI
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface QwotableFileUpdateUtil {
    fun updateLocalVersionNumber(newVersion: Int)
    fun getLocalVersionNumber(): Int
    suspend fun isUpdateAvailable(): Boolean
}

class QwotableFileUpdateUtilImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val qwotableAPI: QwotableAPI
) : QwotableFileUpdateUtil {
    override fun updateLocalVersionNumber(newVersion: Int) {
        val apiPreference = context.getSharedPreferences("api", Context.MODE_PRIVATE)
        apiPreference.edit().putInt("version", newVersion).apply()
    }

    override fun getLocalVersionNumber(): Int {
        val apiPreference = context.getSharedPreferences("api", Context.MODE_PRIVATE)
        return apiPreference.getInt("version", -1)
    }

    override suspend fun isUpdateAvailable(): Boolean {
        val remoteVersion = qwotableAPI.getQwotableVersionInfo()[0].version
        val localAPIVersion = getLocalVersionNumber()

        return if (localAPIVersion == -1) true else localAPIVersion < remoteVersion

    }
}