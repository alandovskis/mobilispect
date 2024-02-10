package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.schedule.agency.FeedLocalAgencyID
import com.mobilispect.backend.schedule.agency.FeedLocalAgencyIDSerializer
import kotlinx.serialization.Serializable

@Serializable
data class GTFSAgency(
    @Serializable(with = FeedLocalAgencyIDSerializer::class)
    val agency_id: FeedLocalAgencyID,

    val agency_name: String
)
