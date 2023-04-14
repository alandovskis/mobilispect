package com.mobilispect.backend.data.transit_land

import kotlinx.serialization.Serializable

@Serializable
internal class TransitLandFeedResponse(val feeds: Collection<TransitLandFeed>, val meta: Meta? = null)
