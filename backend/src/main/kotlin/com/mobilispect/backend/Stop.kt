package com.mobilispect.backend

import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "stops")
data class Stop(val id: String, val name: String, val versions: Collection<String>)
