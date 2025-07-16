package com.mobilispect.backend

import com.mobilispect.backend.schedule.transit_land.internal.client.TransitLandRoute
import kotlinx.serialization.Serializable

@Serializable
internal class TransitLandRouteResponse(val routes: Collection<TransitLandRoute>)
