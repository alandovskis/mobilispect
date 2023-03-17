@file:Suppress("unused") // Deleting any of the unused fields will be break deserialization.

package com.mobilispect.backend.data.transit_land

import kotlinx.serialization.Serializable

@Serializable
internal class Meta(val after: Int, val next: String? = null)