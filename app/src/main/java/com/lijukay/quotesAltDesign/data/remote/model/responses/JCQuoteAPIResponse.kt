package com.lijukay.quotesAltDesign.data.remote.model.responses

import com.lijukay.quotesAltDesign.data.remote.model.JamesClearQuote

data class JCQuoteAPIResponse(
    val rawText: String,
    val text: String,
    val source: String?,
    val clickToTweetId: String
) {
    companion object {
        fun JCQuoteAPIResponse.toQwotable(): JamesClearQuote {
            return JamesClearQuote(
                quote = text,
                source = source ?: ""
            )
        }
    }
}
