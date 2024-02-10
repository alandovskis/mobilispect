package com.mobilispect.backend.schedule.transit_land.internal.client

import kotlinx.serialization.Serializable

@Serializable
internal class TransitLandStopResponse(val stops: Collection<TransitLandStop>, val meta: Meta? = null)
