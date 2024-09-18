package com.lijukay.quotesAltDesign.data.remote.model.responses

import com.lijukay.quotesAltDesign.data.remote.model.StoicQuote

data class TekloonStoicQuotesAPIResponse(
    val data: TekloonStoicQuotesData
) {
    companion object {
        fun TekloonStoicQuotesAPIResponse.toQwotable(): StoicQuote {
            return StoicQuote(
                quote = data.quote,
                author = data.author ?: "",
                apiName = "Tekloon Stoic Source API",
                apiSource = "https://github.com/tlcheah2/stoic-quote-lambda-public-api"
            )
        }
    }
}

data class TekloonStoicQuotesData(
    val quote: String,
    val author: String?
)
