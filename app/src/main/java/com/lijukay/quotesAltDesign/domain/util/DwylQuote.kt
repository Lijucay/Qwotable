package com.lijukay.quotesAltDesign.domain.util

import com.google.gson.annotations.SerializedName
import com.lijukay.quotesAltDesign.data.local.model.LocalQwotable

data class DwylQuote(
    @SerializedName("text")
    override var quote: String,
    override var author: String
) : LocalQwotable {
    override var language: String = ""
    override var isFavorite: Boolean = false
    override var hasAuthor: Boolean = true
    override var hasSource: Boolean = true
    override var source: String = "DwylQuotes"
}
