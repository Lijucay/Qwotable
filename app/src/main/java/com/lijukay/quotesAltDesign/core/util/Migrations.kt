package com.lijukay.quotesAltDesign.core.util

import androidx.room.migration.Migration

val MIGRATION_1_2 = Migration(1, 2) { database ->
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