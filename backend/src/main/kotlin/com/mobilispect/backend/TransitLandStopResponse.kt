package com.mobilispect.backend

import kotlinx.serialization.Serializable

@Serializable
internal class TransitLandStopResponse(val stops: Collection<TransitLandStop>)
