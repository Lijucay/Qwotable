package com.lijukay.quotesAltDesign.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A class, that represents an DBQwotable. A DBQwotable is a locally stored Qwotable, that is not
 * user defined and comes from the remote Qwotable file.
 *
 * DBQwotable inherits from [LocalQwotable]
 */
@Entity(indices = [Index(value = ["quote"], unique = true)])
data class DBQwotable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    override var quote: String,
    override var author: String,
    override var source: String,
    override var language: String,
    @ColumnInfo(defaultValue = "0")
    override var isFavorite: Boolean = false,
) : LocalQwotable {
    @Ignore
    override var hasAuthor: Boolean = author != ""
    @Ignore
    override var hasSource: Boolean = source != ""
}