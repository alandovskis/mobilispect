package com.mobilispect.backend.data.feed


/**
 * The combined of a [Feed] and its [FeedVersion].
 */
data class VersionedFeed(val feed: Feed, val version: FeedVersion)
