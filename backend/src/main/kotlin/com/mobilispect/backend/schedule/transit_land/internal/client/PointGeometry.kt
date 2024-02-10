@file:Suppress("unused") // Deleting any of the unused fields will be break deserialization.

package com.mobilispect.backend.schedule.transit_land.internal.client

import kotlinx.serialization.Serializable

@Serializable
internal class PointGeometry(val coordinates: Array<Double>, val type: String)
