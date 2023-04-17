package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.agency.OneStopAgencyIDDataSource

/**
 * A [OneStopAgencyIDDataSource] that uses transit.land for agency IDs.
 */
class TransitLandOneStopAgencyIDDataSource(
    private val transitLandClient: TransitLandClient,
    private val transitLandCredentialsRepository: TransitLandCredentialsRepository,
) : OneStopAgencyIDDataSource {
    override fun agencyIDs(feedID: String): Result<Map<String, String>> {
        val apiKey = transitLandCredentialsRepository.get() ?: return Result.failure(Exception("Missing API key"))
        return transitLandClient.agencies(apiKey = apiKey, feedID = feedID)
            .map { agencies ->
                agencies.agencies.associate { item -> item.agencyID to item.id }
            }
    }
}