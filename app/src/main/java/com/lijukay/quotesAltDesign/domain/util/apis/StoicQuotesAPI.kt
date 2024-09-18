package com.lijukay.quotesAltDesign.domain.util.apis

import com.lijukay.quotesAltDesign.data.remote.model.responses.StoicQuotesAPIResponse
import retrofit2.http.GET

interface StoicQuotesAPI {
    @GET("api/quote")
    suspend fun getRandomQuote(): StoicQuotesAPIResponse
}