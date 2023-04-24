package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.agency.FeedLocalAgencyID
import com.mobilispect.backend.data.agency.FeedLocalAgencyIDSerializer
import kotlinx.serialization.Serializable

@Serializable
data class GTFSAgency(
    @Serializable(with = FeedLocalAgencyIDSerializer::class)
    val agency_id: FeedLocalAgencyID,

    val agency_name: String
)
