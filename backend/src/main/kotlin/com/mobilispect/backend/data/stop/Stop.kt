package com.mobilispect.backend.data.stop

import com.mobilispect.backend.batch.Entity
import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "stops")
data class Stop(override val _id: String, val name: String, override val version: String) : Entity
