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

package com.lijukay.quotesAltDesign

import android.app.Application
import android.content.Intent
import com.lijukay.core.database.QwotableDatabase
import com.lijukay.core.utils.QwotableApiService
import com.lijukay.core.utils.QwotableRepository
import com.lijukay.core.utils.UncaughtExceptionHandler
import com.lijukay.quotesAltDesign.ui.navigation.activities.LogActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {
    private val retrofitQwotable: Retrofit = Retrofit.Builder()
        .baseUrl("https://lijucay.github.io")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: QwotableApiService =
        retrofitQwotable.create(QwotableApiService::class.java)

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