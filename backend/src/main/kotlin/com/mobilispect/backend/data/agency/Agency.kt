package com.mobilispect.backend.data.agency

import com.mobilispect.backend.batch.Entity
import org.springframework.data.mongodb.core.mapping.Document

private val ONESTOP_ID_REGEX = Regex("o-[a-z0-9]+-[a-z0-9~çéâêîôûàèùëïü]+")

@Suppress("ConstructorParameterNaming") // For _id
@Document(value = "agencies")
data class Agency(
    /**
     * ID (in OneStop ID format ex: o-geohash-name)
     */
    override val _id: String,
    val name: String,
    override val version: String
) : Entity {
    init {
        require(ONESTOP_ID_REGEX.matches(_id)) { "Must be in OneStopID format i.e. o-geohash-name. Was $_id" }
    }
}
