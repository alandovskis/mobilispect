package com.mobilispect.backend.schedule.feed

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(value = "feed_versions")
@Suppress("ConstructorParameterNaming") // For _id
data class FeedVersion(val _id: String, val feedID: String, val startsOn: LocalDate, val endsOn: LocalDate)
