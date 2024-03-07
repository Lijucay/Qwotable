package com.lijukay.quotesAltDesign

import android.app.Application
import com.lijukay.core.database.QwotableDatabase
import com.lijukay.core.utils.QwotableApiService
import com.lijukay.core.utils.QwotableRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {
    private val retrofitQwotable: Retrofit = Retrofit.Builder()
        .baseUrl("https://lijucay.github.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: QwotableApiService = retrofitQwotable.create(QwotableApiService::class.java)

    private val database by lazy { QwotableDatabase.getDatabase(this) }
    val repository by lazy {
        QwotableRepository(
            database.qwotableDao(),
            apiService,
            applicationContext
        )
    }
}