package com.lijukay.quotesAltDesign.domain.util.apis

import com.lijukay.quotesAltDesign.data.remote.model.responses.KanyeRestAPIResponse
import retrofit2.http.GET

interface KanyeRestAPI {
    @GET("/")
    suspend fun getRandomQuote(): KanyeRestAPIResponse
}