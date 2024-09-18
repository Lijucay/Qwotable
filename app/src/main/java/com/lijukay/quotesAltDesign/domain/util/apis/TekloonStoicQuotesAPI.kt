package com.lijukay.quotesAltDesign.domain.util.apis

import com.lijukay.quotesAltDesign.data.remote.model.responses.TekloonStoicQuotesAPIResponse
import retrofit2.http.GET

interface TekloonStoicQuotesAPI {
    @GET("stoic-quote")
    suspend fun getRandomQuote(): TekloonStoicQuotesAPIResponse
}