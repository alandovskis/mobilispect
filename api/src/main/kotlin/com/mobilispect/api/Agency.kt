package com.mobilispect.api

import org.springframework.data.mongodb.core.mapping.Document

@Suppress("PropertyName") // For _id
@Document(value = "agencies")
data class Agency(val _id: String, val name: String)
