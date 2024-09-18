package com.lijukay.quotesAltDesign.data.remote.model

data class JamesClearQuote(
    override var quote: String,
    override var source: String
): RemoteQwotable {
    override var author: String = "James Clear"
    override var language: String = ""
    override var hasAuthor: Boolean = false
    override var hasSource: Boolean = source.isNotBlank()
    override var apiName: String = "James Clear Quotes API"
    override var apiSource: String = "https://github.com/MauricioRobayo/jcquotes"

}
