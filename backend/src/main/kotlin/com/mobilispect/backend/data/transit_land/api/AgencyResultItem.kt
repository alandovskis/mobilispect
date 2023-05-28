package com.mobilispect.backend.data.transit_land.api

import com.mobilispect.backend.data.agency.FeedLocalAgencyID
import com.mobilispect.backend.data.agency.OneStopAgencyID

data class AgencyResultItem(
    val id: OneStopAgencyID,
    val name: String,
    val version: String,
    val feedID: String,
    val agencyID: FeedLocalAgencyID
)

