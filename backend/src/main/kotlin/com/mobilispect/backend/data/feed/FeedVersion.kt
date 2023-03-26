package com.mobilispect.backend.data.feed

import java.time.LocalDate

data class FeedVersion(val feedID: String, val version: String, val startsOn: LocalDate, val endsOn: LocalDate)