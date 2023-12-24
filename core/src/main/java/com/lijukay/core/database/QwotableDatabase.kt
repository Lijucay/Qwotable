package com.lijukay.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Quote::class, Wisdom::class], version = 1, exportSchema = false)
abstract class QwotableDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
    abstract fun wisdomDao(): WisdomDao

    companion object {
        @Volatile
        private var INSTANCE: QwotableDatabase? = null

        fun getDatabase(context: Context): QwotableDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QwotableDatabase::class.java,
                    "qwotable_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}