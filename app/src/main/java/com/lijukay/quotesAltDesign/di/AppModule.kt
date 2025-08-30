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

package com.lijukay.quotesAltDesign.di

import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lijukay.quotesAltDesign.core.util.MIGRATION_1_2
import com.lijukay.quotesAltDesign.data.local.QwotableDatabase
import com.lijukay.quotesAltDesign.data.repository.QwotableRepository
import com.lijukay.quotesAltDesign.data.repository.QwotableRepositoryImpl
import com.lijukay.quotesAltDesign.domain.util.ConnectionHelper
import com.lijukay.quotesAltDesign.domain.util.ConnectionHelperImpl
import com.lijukay.quotesAltDesign.domain.util.QwotableFileUpdateUtil
import com.lijukay.quotesAltDesign.domain.util.QwotableFileUpdateUtilImpl
import com.lijukay.quotesAltDesign.domain.util.RandomQuote
import com.lijukay.quotesAltDesign.domain.util.RandomQuoteImpl
import com.lijukay.quotesAltDesign.domain.util.apis.GameOfThronesAPI
import com.lijukay.quotesAltDesign.domain.util.apis.KanyeRestAPI
import com.lijukay.quotesAltDesign.domain.util.apis.QwotableAPI
import com.lijukay.quotesAltDesign.domain.util.apis.StoicQuotesAPI
import com.lijukay.quotesAltDesign.domain.util.apis.TekloonStoicQuotesAPI
import com.lijukay.quotesAltDesign.presentation.viewmodels.QwotableViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.Executors

val appModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://lijucay.github.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<QwotableAPI>()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.kanye.rest")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<KanyeRestAPI>()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.gameofthronesquotes.xyz/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<GameOfThronesAPI>()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://stoic.tekloon.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<TekloonStoicQuotesAPI>()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://stoic-quotes.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<StoicQuotesAPI>()
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            QwotableDatabase::class.java,
            "qwotable_database"
        )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .setQueryCallback(
                { sqlQuery, bindArgs -> Log.d("Query", "Query: $sqlQuery, SQL ARGS: $bindArgs") },
                Executors.newSingleThreadExecutor()
            )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    singleOf(::ConnectionHelperImpl).bind<ConnectionHelper>()

    single { get<QwotableDatabase>().qwotableDao() }

    singleOf(::QwotableFileUpdateUtilImpl).bind<QwotableFileUpdateUtil>()
    singleOf(::RandomQuoteImpl).bind<RandomQuote>()
    singleOf(::QwotableRepositoryImpl).bind<QwotableRepository>()

    viewModelOf(::QwotableViewModel)
}