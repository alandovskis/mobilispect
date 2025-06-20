package com.mobilispect.backend.schedule.agency

interface AgencyIDDataSource {
    fun agencyIDs(feedID: String): Result<Map<FeedLocalAgencyID, OneStopAgencyID>>
}
