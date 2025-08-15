package com.mobilispect.backend.schedule.feed

import com.mobilispect.backend.Feed
import com.mobilispect.backend.FeedVersion

data class VersionedFeed(val feed: Feed, val version: FeedVersion)
