package com.mobilispect.backend.schedule.feed


/**
 * The combined of a [Feed] and its [FeedVersion].
 */
data class VersionedFeed(val feed: Feed, val version: FeedVersion)
