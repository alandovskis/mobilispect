package com.mobilispect.backend.schedule.transit_land.internal.client

import kotlinx.serialization.Serializable

@Serializable
internal class TransitLandRouteResponse(val routes: Collection<TransitLandRoute>, val meta: Meta? = null)
