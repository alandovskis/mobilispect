package com.mobilispect.backend.data.agency

import com.mobilispect.backend.batch.Entity
import org.springframework.data.mongodb.core.mapping.Document

@Suppress("ConstructorParameterNaming") // For _id
@Document(value = "agencies")
data class Agency(override val _id: String, val name: String, override val version: String) : Entity
