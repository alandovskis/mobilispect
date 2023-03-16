package com.mobilispect.backend.data

import org.springframework.data.mongodb.core.mapping.Document

@Suppress("PropertyName", "ConstructorParameterNaming") // For _id
@Document(value = "agencies")
data class Agency(val _id: String, val name: String, val version: String)
