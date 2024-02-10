package com.mobilispect.backend.schedule.region

import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "regions")
@Suppress("ConstructorParameterNaming") // For _id
data class Region(
    /**
     * A ID in OneStopID format (ex: reg-geohash-city)
     */
    val _id: String,
    val name: String
)
