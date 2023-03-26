package com.mobilispect.backend.data.feed

import com.mobilispect.backend.data.Identified
import org.springframework.data.mongodb.core.mapping.Document

@Document("feeds")
data class Feed(override val _id: String, val name: String, val url: String) : Identified