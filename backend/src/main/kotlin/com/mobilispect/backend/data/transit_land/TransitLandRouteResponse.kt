package com.mobilispect.backend.data.transit_land

import kotlinx.serialization.Serializable

@Serializable
internal class TransitLandRouteResponse(val routes: Collection<TransitLandRoute>, val meta: Meta? = null)
