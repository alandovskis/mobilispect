package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.stop.StopIDDataSource
import com.mobilispect.backend.data.transit_land.api.StopResultItem
import com.mobilispect.backend.data.transit_land.api.TransitLandCredentialsRepository
import com.mobilispect.backend.data.transit_land.internal.client.TransitLandClient

/**
 * A [StopIDDataSource] that uses transit.land for stop IDs.
 */
class TransitLandStopIDDataSource(
    private val transitLandClient: TransitLandClient,
    private val transitLandCredentialsRepository: TransitLandCredentialsRepository
) : StopIDDataSource {
    override fun stops(feedID: String): Result<Map<String, String>> {
        return findStopIDs(feedID)
            .map { stops ->
                stops.fold(mutableMapOf()) { acc, item ->
                    acc[item.stopID] = item.id
                    acc
                }
            }
    }

    @Suppress("ReturnCount")
    private fun findStopIDs(feedID: String): Result<Collection<StopResultItem>> {
        val apiKey = transitLandCredentialsRepository.get() ?: return Result.failure(Exception("Missing API key"))
        val allStops = mutableListOf<StopResultItem>()
        var after: Int? = null
        do {
            val stopsRes = transitLandClient.stops(
                apiKey = apiKey,
                feedID = feedID,
                paging = PagingParameters(limit = 100, after = after)
            )
                .map {
                    after = it.after
                    return@map it.stops
                }
            if (stopsRes.isFailure) {
                return stopsRes
            }

            val stops = stopsRes.getOrNull()!!
            allStops += stops

        } while (stops.isNotEmpty())
        return Result.success(allStops)
    }
}
