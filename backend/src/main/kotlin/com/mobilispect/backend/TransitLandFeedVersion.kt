package com.mobilispect.backend

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@ExperimentalSerializationApi
@Serializable
class TransitLandVersionedFeed(
    val feed: TransitLandFeedRef, val feedVersion: TransitLandFeedVersionRef
)