/*
* Copyright (C) 2024 Lijucay (Luca)
*
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation, either version 3 of the License, or
*   (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*   along with this program.  If not, see <https://www.gnu.org/licenses/>
* */

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