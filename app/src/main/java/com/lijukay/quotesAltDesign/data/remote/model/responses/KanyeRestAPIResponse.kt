package com.lijukay.quotesAltDesign.data.remote.model.responses

import com.lijukay.quotesAltDesign.data.remote.model.KanyeWestQuote

data class KanyeRestAPIResponse(
    val quote: String
) {
    companion object {
        fun KanyeRestAPIResponse.toQwotable(): KanyeWestQuote {
            return KanyeWestQuote(quote)
        }
    }
}