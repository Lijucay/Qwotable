package com.lijukay.quotesAltDesign

import android.app.Application
import android.content.Intent
import com.lijukay.core.database.QwotableDatabase
import com.lijukay.core.utils.QwotableApiService
import com.lijukay.core.utils.QwotableRepository
import com.lijukay.core.utils.UncaughtExceptionHandler
import com.lijukay.quotesAltDesign.ui.navigation.LogActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {
    private val retrofitQwotable: Retrofit = Retrofit.Builder()
        .baseUrl("https://lijucay.github.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: QwotableApiService = retrofitQwotable.create(QwotableApiService::class.java)

    private val database by lazy { QwotableDatabase.getDatabase(context = this) }
    val repository by lazy {
        QwotableRepository(
            qwotableDao = database.qwotableDao(),
            apiService = apiService,
            context = applicationContext
        )
    }

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(
            UncaughtExceptionHandler(
                context = this,
                logIntent = Intent(this, LogActivity::class.java),
                versionCode = BuildConfig.VERSION_CODE,
                versionName = BuildConfig.VERSION_NAME
            )
        )
    }
}