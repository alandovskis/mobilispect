package com.mobilispect.backend.data.region

import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "regions")
data class Region(
    /**
     * A ID in OneStopID format (ex: reg-geohash-city)
     */
    val _id: String,
    val name: String
)
