package com.mobilispect.backend.infastructure

import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "stops")
data class Stop(val uid: String, val localID: String, val name: String, val versions: Collection<String>)
