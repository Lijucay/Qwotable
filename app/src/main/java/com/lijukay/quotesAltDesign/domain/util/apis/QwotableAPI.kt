package com.lijukay.quotesAltDesign.domain.util.apis

import com.lijukay.quotesAltDesign.data.remote.model.responses.QwotableVersionInfo
import com.lijukay.quotesAltDesign.data.remote.model.responses.RemoteQwotable
import retrofit2.http.GET

interface QwotableAPI {
    @GET("Qwotable/qwotables_schema2.json")
    suspend fun getQwotables(): List<RemoteQwotable>

    @GET("Qwotable/QwotableVersion.json")
    suspend fun getQwotableVersionInfo(): List<QwotableVersionInfo>
}