package com.mobilispect.backend.data.route

/**
 * A route ID that is only unique within a feed.
 */
@JvmInline
value class FeedLocalRouteID(internal val str: String)