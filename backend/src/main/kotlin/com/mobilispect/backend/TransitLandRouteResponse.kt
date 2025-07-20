package com.mobilispect.backend

import kotlinx.serialization.Serializable

@Serializable
internal class TransitLandRouteResponse(val meta: Meta? = null, val routes: Collection<TransitLandRoute>)
