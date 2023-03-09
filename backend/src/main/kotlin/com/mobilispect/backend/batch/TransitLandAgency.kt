@file:OptIn(ExperimentalSerializationApi::class)

package com.mobilispect.backend.batch

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
internal data class TransitLandAgencyResponse(val agencies: Collection<TransitLandAgency>, val meta: Meta)

@Serializable
data class Meta(val after: Int)

@Serializable
data class TransitLandAgency(
    @JsonNames("agency_name") val name: String,
    @JsonNames("onestop_id") val onestopID: String,
    @JsonNames("feed_version") val feed: Feed
)

@Serializable
data class Feed(
    @JsonNames("sha1") val version: String
)
