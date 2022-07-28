package com.mobilispect.data.transit_land

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