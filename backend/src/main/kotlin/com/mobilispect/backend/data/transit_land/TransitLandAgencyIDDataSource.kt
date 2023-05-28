package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.agency.AgencyIDDataSource
import com.mobilispect.backend.data.agency.FeedLocalAgencyID
import com.mobilispect.backend.data.agency.OneStopAgencyID
import com.mobilispect.backend.data.transit_land.api.TransitLandCredentialsRepository
import com.mobilispect.backend.data.transit_land.internal.client.TransitLandClient

/**
 * A [AgencyIDDataSource] that uses transit.land for agency IDs.
 */
class TransitLandAgencyIDDataSource(
    private val transitLandClient: TransitLandClient,
    private val transitLandCredentialsRepository: TransitLandCredentialsRepository,
) : AgencyIDDataSource {
    override fun agencyIDs(feedID: String): Result<Map<FeedLocalAgencyID, OneStopAgencyID>> {
        val apiKey = transitLandCredentialsRepository.get() ?: return Result.failure(Exception("Missing API key"))
        return transitLandClient.agencies(apiKey = apiKey, feedID = feedID)
            .map { agencies ->
                agencies.agencies.associate { item -> item.agencyID to item.id }
            }
    }
}
