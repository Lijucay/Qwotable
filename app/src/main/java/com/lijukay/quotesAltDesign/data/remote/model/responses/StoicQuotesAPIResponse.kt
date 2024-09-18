package com.lijukay.quotesAltDesign.data.remote.model.responses

import com.lijukay.quotesAltDesign.data.remote.model.StoicQuote

data class StoicQuotesAPIResponse(
    val text: String,
    val author: String?
) {
    companion object {
        fun StoicQuotesAPIResponse.toQwotable(): StoicQuote {
            return StoicQuote(
                quote = text,
                author = author ?: "",
                apiName = "Stoic Quotes API",
                apiSource = "https://stoic-quotes.com/"
            )
        }
    }
}