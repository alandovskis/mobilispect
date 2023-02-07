package com.mobilispect.api

import org.springframework.data.mongodb.core.mapping.Document

@Suppress("PropertyName", "ConstructorParameterNaming") // For _id
@Document(value = "routes")
data class Route(val _id: String, val shortName: String, val longName: String, val agencyID: String)
