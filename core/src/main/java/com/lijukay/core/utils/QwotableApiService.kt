package com.lijukay.core.utils

import com.google.gson.annotations.SerializedName
import com.lijukay.core.database.Qwotable
import retrofit2.http.GET

interface QwotableApiService {
    @GET("Qwotable/qwotables.json")
    suspend fun getQwotables(): List<Qwotable>

    @GET("PrUp/Qwotable.json")
    suspend fun getVersionsFile(): JsonFilesResponse
}

data class JsonFilesResponse(
    @SerializedName("JSON Files") val jsonFiles: List<JsonFile>
)

data class JsonFile(
    @SerializedName("QwotableJSONVersion") val qwotableJsonVersion: Int
)

data class StringValueResponse(
     @SerializedName("Quotes JSON Version") val quotesJsonVersion: Int
)