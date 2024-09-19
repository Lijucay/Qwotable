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

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.lijukay.quotesAltDesign.data.local.QwotableDao
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
import com.lijukay.quotesAltDesign.domain.util.apis.JCQuotesAPI
import com.lijukay.quotesAltDesign.domain.util.apis.KanyeRestAPI
import com.lijukay.quotesAltDesign.domain.util.apis.QwotableAPI
import com.lijukay.quotesAltDesign.domain.util.apis.StoicQuotesAPI
import com.lijukay.quotesAltDesign.domain.util.apis.TekloonStoicQuotesAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideQwotableAPI(): QwotableAPI =
        Retrofit.Builder()
            .baseUrl("https://lijucay.github.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<QwotableAPI>()

    @Singleton
    @Provides
    fun provideKanyeRestAPI(): KanyeRestAPI =
        Retrofit.Builder()
            .baseUrl("https://api.kanye.rest")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<KanyeRestAPI>()

    @Singleton
    @Provides
    fun provideGameOfThronesAPI(): GameOfThronesAPI =
        Retrofit.Builder()
            .baseUrl("https://api.gameofthronesquotes.xyz/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<GameOfThronesAPI>()

    @Singleton
    @Provides
    fun provideJCQuotesAPI(): JCQuotesAPI =
        Retrofit.Builder()
            .baseUrl("https://www.jcquotes.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<JCQuotesAPI>()

    @Singleton
    @Provides
    fun provideTekloonStoicQuotesAPI(): TekloonStoicQuotesAPI =
        Retrofit.Builder()
            .baseUrl("https://stoic.tekloon.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<TekloonStoicQuotesAPI>()

    @Singleton
    @Provides
    fun provideStoicQuotesAPI(): StoicQuotesAPI =
        Retrofit.Builder()
            .baseUrl("https://stoic-quotes.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<StoicQuotesAPI>()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): QwotableDatabase {
        return Room.databaseBuilder(
            context,
            QwotableDatabase::class.java,
            "qwotable_database"
        )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .setQueryCallback(
                { sqlQuery, bindArgs -> Log.d("Query", "Query: $sqlQuery, SQL ARGS: $bindArgs") },
                Executors.newSingleThreadExecutor()
            )
            .addMigrations(
                Migration(1, 2) { database ->
                    database.execSQL(
                        """
                            CREATE TABLE IF NOT EXISTS DBQwotable (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                quote TEXT NOT NULL,
                                author TEXT NOT NULL,
                                source TEXT NOT NULL,
                                language TEXT NOT NULL,
                                isFavorite INTEGER NOT NULL DEFAULT 0,
                                CONSTRAINT unique_quote UNIQUE (quote)
                            );
                        """.trimIndent()
                    )

                    database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_DBQwotable_quote ON DBQwotable (quote ASC);")

                    database.execSQL(
                        """
                            CREATE TABLE IF NOT EXISTS OwnQwotable (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                quote TEXT NOT NULL,
                                author TEXT NOT NULL,
                                source TEXT NOT NULL,
                                isFavorite INTEGER NOT NULL DEFAULT 0,
                                CONSTRAINT unique_quote UNIQUE (quote)
                            );
                        """.trimIndent()
                    )

                    database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_OwnQwotable_quote ON OwnQwotable (quote ASC)")

                    database.execSQL(
                        """
                            INSERT INTO DBQwotable (id, quote, author, source, language, isFavorite)
                            SELECT id, qwotable AS quote, author, source, language, isFavorite
                            FROM Qwotable
                            WHERE isOwn = 0
                        """
                    )

                    database.execSQL(
                        """
                            INSERT INTO OwnQwotable (id, quote, author, source, isFavorite)
                            SELECT id, qwotable AS quote, author, source, isFavorite
                            FROM Qwotable
                            WHERE isOwn = 1
                        """
                    )

                    database.execSQL("DROP TABLE Qwotable")
                }
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideConnectionHelper(
        @ApplicationContext context: Context
    ): ConnectionHelper {
        return ConnectionHelperImpl(context)
    }

    @Singleton
    @Provides
    fun provideQwotableDao(database: QwotableDatabase): QwotableDao {
        return database.qwotableDao()
    }

    @Singleton
    @Provides
    fun provideQwotableFileUpdateUtil(
        @ApplicationContext context: Context,
        qwotableAPI: QwotableAPI
    ): QwotableFileUpdateUtil {
        return QwotableFileUpdateUtilImpl(context, qwotableAPI)
    }

    @Singleton
    @Provides
    fun provideQwotableRepository(
        qwotableDao: QwotableDao,
        qwotableAPI: QwotableAPI,
        connectionHelper: ConnectionHelper,
        qwotableFileUpdateUtil: QwotableFileUpdateUtil,
        randomQuote: RandomQuote
    ): QwotableRepository {
        return QwotableRepositoryImpl(
            qwotableAPI = qwotableAPI,
            qwotableDao = qwotableDao,
            connectionHelper = connectionHelper,
            qwotableFileUpdateUtil = qwotableFileUpdateUtil,
            randomQuote = randomQuote
        )
    }

    @Singleton
    @Provides
    fun provideRandomQuote(
        @ApplicationContext context: Context,
        connectionHelper: ConnectionHelper,
        qwotableDao: QwotableDao,
        kanyeRestAPI: KanyeRestAPI,
        gameOfThronesAPI: GameOfThronesAPI,
        tekloonStoicQuotesAPI: TekloonStoicQuotesAPI,
        jcQuotesAPI: JCQuotesAPI,
        stoicQuotesAPI: StoicQuotesAPI
    ): RandomQuote {
        return RandomQuoteImpl(
            context,
            connectionHelper,
            qwotableDao,
            kanyeRestAPI,
            gameOfThronesAPI,
            tekloonStoicQuotesAPI,
            jcQuotesAPI,
            stoicQuotesAPI
        )
    }
}