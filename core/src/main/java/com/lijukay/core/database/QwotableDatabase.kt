package com.lijukay.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Qwotable::class], version = 1, exportSchema = false)
abstract class QwotableDatabase: RoomDatabase() {
    abstract fun qwotableDao(): QwotableDao

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
                return@synchronized instance
            }
        }
    }
}