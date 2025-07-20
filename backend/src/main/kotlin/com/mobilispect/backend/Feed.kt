package com.mobilispect.backend

import org.springframework.data.mongodb.core.mapping.Document

@Document("feeds")
data class Feed(val uid: String, val url: String)
