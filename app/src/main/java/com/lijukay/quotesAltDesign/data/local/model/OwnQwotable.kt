package com.lijukay.quotesAltDesign.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A representation of a user-defined Qwotable.
 *
 * OwnQwotable inherits from [LocalQwotable]
 * */
@Entity(indices = [Index(value = ["quote"], unique = true)])
data class OwnQwotable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    override var quote: String,
    override var author: String,
    override var source: String,
    @ColumnInfo(defaultValue = "0")
    override var isFavorite: Boolean = false
) : LocalQwotable {
    @Ignore override var hasSource: Boolean = source.isNotBlank()
    @Ignore override var language: String = ""
    @Ignore override var hasAuthor: Boolean = author.isNotBlank()
}
