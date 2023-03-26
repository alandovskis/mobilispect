package com.mobilispect.backend.data.feed


/**
 * The combined of a [Feed] and its [FeedVersion].
 */
class VersionedFeed(val feed: Feed, val version: FeedVersion)