package com.lijukay.quotesAltDesign.data.remote.model

data class KanyeWestQuote(
    override var quote: String
): RemoteQwotable {
    override var author: String = ""
    override var apiName: String = "Kanye West Quote API"
    override var apiSource: String = "https://github.com/ajzbc/kanye.rest"
    override var source: String = ""
    override var language: String = ""
    override var hasAuthor: Boolean = false
    override var hasSource: Boolean = false

}
