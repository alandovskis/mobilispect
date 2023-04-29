package com.mobilispect.backend.data.agency

interface AgencyIDDataSource {
    fun agencyIDs(feedID: String): Result<Map<FeedLocalAgencyID, OneStopAgencyID>>
}
