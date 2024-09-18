package com.lijukay.quotesAltDesign.domain.util.apis

import com.lijukay.quotesAltDesign.data.remote.model.responses.GameOfThronesAPIResponse
import retrofit2.http.GET

interface GameOfThronesAPI {
    @GET("v1/random")
    suspend fun getRandomQuote(): GameOfThronesAPIResponse
}