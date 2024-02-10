package com.mobilispect.backend.schedule.transit_land.api

import com.mobilispect.backend.schedule.agency.FeedLocalAgencyID
import com.mobilispect.backend.schedule.agency.OneStopAgencyID

data class AgencyResultItem(
    val id: OneStopAgencyID,
    val name: String,
    val version: String,
    val feedID: String,
    val agencyID: FeedLocalAgencyID
)

