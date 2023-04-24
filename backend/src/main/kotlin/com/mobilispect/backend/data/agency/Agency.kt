package com.mobilispect.backend.data.agency

import org.springframework.data.mongodb.core.mapping.Document

@Suppress("ConstructorParameterNaming") // For _id
@Document(value = "agencies")
data class Agency(
    /**
     * ID (in OneStop ID format ex: o-geohash-name)
     */
    val _id: OneStopAgencyID,
    val name: String,
    val version: String
)
