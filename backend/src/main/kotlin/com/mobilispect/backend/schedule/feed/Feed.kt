package com.mobilispect.backend.schedule.feed

import org.springframework.data.mongodb.core.mapping.Document

@Document("feeds")
data class Feed(val _id: String, val url: String)
