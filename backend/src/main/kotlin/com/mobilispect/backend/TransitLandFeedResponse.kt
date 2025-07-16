package com.mobilispect.backend

import kotlinx.serialization.Serializable

@Serializable
internal class TransitLandFeedResponse(val feeds: Collection<TransitLandFeed>)
