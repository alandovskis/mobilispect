package com.mobilispect.common.data.core.transitland

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TransitLandAPI {
    @GET("/api/v2/rest/routes/{route}")
    suspend fun fromRef(
        @Path("route") routeRef: String,
        @Header("apikey") apiKey: String
    ): TransitLandRouteResponse
}