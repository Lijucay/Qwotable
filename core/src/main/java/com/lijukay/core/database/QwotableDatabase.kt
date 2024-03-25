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