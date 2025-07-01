package com.mobilispect.backend

import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "regions")
data class Region(
    /**
     * A ID in OneStopID format (ex: reg-geohash-city)
     */
    val uid: String,
    val name: String
)
