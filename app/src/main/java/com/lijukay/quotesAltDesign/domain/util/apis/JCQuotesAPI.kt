package com.lijukay.quotesAltDesign.domain.util.apis

import com.lijukay.quotesAltDesign.data.remote.model.responses.JCQuoteAPIResponse
import retrofit2.http.GET

interface JCQuotesAPI {
    @GET("api/quotes/random")
    suspend fun getRandomQuote(): JCQuoteAPIResponse
}