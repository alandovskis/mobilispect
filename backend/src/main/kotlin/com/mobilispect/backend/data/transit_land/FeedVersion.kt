@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("unused") // Deleting any of the unused fields will be break deserialization.

package com.mobilispect.backend.data.transit_land

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
internal class FeedVersion(
    val feed: Feed,
    @JsonNames("fetched_at") val fetchedAt: String? = null,
    @JsonNames("id") val id: Int? = null,
    @JsonNames("sha1") val version: String
)

@Serializable
internal class Feed(val id: Int? = null, @JsonNames("onestop_id") val oneStopID: String)
