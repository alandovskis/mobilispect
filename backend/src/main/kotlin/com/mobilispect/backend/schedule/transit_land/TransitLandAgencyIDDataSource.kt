package com.mobilispect.backend.schedule.transit_land

import com.mobilispect.backend.schedule.agency.AgencyIDDataSource
import com.mobilispect.backend.schedule.agency.FeedLocalAgencyID
import com.mobilispect.backend.schedule.agency.OneStopAgencyID
import com.mobilispect.backend.schedule.transit_land.api.TransitLandAPI
import com.mobilispect.backend.schedule.transit_land.api.TransitLandCredentialsRepository

/**
 * A [AgencyIDDataSource] that uses transit.land for agency IDs.
 */
class TransitLandAgencyIDDataSource(
    private val transitLandAPI: TransitLandAPI,
    private val transitLandCredentialsRepository: TransitLandCredentialsRepository,
) : AgencyIDDataSource {
    override fun agencyIDs(feedID: String): Result<Map<FeedLocalAgencyID, OneStopAgencyID>> {
        val apiKey = transitLandCredentialsRepository.get() ?: return Result.failure(Exception("Missing API key"))
        return transitLandAPI.agencies(apiKey = apiKey, feedID = feedID)
            .map { agencies ->
                agencies.agencies.associate { item -> item.agencyID to item.id }
            }
    }
}
