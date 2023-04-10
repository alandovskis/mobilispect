@file:Suppress("unused") // Deleting any of the unused fields will be break deserialization.

package com.mobilispect.backend.data.transit_land

import kotlinx.serialization.Serializable

@Serializable
internal class PointGeometry(val coordinates: Array<Double>, val type: String)
