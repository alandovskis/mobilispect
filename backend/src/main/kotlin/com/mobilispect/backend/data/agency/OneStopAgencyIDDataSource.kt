package com.mobilispect.backend.data.agency

interface OneStopAgencyIDDataSource {
    fun agencyIDs(feedID: String): Result<Map<String, String>>
}
