package com.mobilispect.backend

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@ExperimentalSerializationApi
@Serializable
class TransitLandFeedRef(
    @JsonNames("onestop_id")
    val id: String,
)
