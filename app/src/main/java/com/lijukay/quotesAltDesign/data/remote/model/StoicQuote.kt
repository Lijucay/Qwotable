package com.lijukay.quotesAltDesign.data.remote.model

data class StoicQuote(
    override var quote: String,
    override var author: String,
    override var apiName: String,
    override var apiSource: String
) : RemoteQwotable {
    override var source: String = ""
    override var language: String = ""
    override var isFavorite: Boolean = false
    override var hasAuthor: Boolean = author.isNotBlank()
    override var hasSource: Boolean = false
}


