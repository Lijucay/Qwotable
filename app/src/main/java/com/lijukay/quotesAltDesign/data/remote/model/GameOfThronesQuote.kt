package com.lijukay.quotesAltDesign.data.remote.model

data class GameOfThronesQuote(
    override var quote: String,
    override var author: String
) : RemoteQwotable {
    override var apiName: String = "Game of Thrones Quote"
    override var apiSource: String = "https://github.com/shevabam/game-of-thrones-quotes-api"
    override var source: String = ""
    override var language: String = ""
    override var hasAuthor: Boolean = author.isNotBlank()
    override var hasSource: Boolean = false
}