package com.mobilispect.backend.data.agency

data class AgencyResultItem(
    val id: OneStopAgencyID,
    val name: String,
    val version: String,
    val feedID: String,
    val agencyID: FeedLocalAgencyID
)

