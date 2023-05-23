package com.mobilispect.backend.data.feed

import org.springframework.data.mongodb.core.mapping.Document

@Document("feeds")
data class Feed(val _id: String, val url: String)
