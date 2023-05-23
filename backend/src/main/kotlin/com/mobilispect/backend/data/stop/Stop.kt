package com.mobilispect.backend.data.stop

import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "stops")
data class Stop(val _id: String, val name: String, val version: String)
