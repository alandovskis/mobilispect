package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.stop.OneStopStopIDDataSource
import com.mobilispect.backend.data.stop.StopIDMap
import com.mobilispect.backend.data.stop.StopResultItem

/**
 * A [OneStopStopIDDataSource] that uses transit.land for stop IDs.
 */
class TransitLandOneStopStopIDDataSource(
    private val transitLandClient: TransitLandClient,
    private val transitLandCredentialsRepository: TransitLandCredentialsRepository
) : OneStopStopIDDataSource {
    override fun stops(feedID: String): Result<StopIDMap> {
        return findStopIDs(feedID)
            .map { stops ->
                stops.fold(StopIDMap()) { acc, item ->
                    acc.add(item.stopID, item.id)
                    acc
                }
            }
    }

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