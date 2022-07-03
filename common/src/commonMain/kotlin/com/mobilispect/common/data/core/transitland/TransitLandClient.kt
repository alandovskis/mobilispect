package com.mobilispect.common.data.core.transitland

import java.io.IOException
import javax.inject.Inject

class TransitLandClient @Inject constructor(private val transitLandAPI: TransitLandAPI,
                                            private val configRepository: TransitLandConfigRepository
) {
    suspend fun fromRef(
        routeRef: String,
    ): Result<TransitLandRouteResponse> {
        val config = configRepository.config() ?: return Result.failure(Exception("Missing config"))
        return try {
            val value = transitLandAPI.fromRef(routeRef, config.apiKey)
            Result.success(value)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}