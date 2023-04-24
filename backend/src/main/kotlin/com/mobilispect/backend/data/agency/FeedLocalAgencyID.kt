package com.mobilispect.backend.data.agency

/**
 * An agency ID that is only unique within a feed.
 */
@JvmInline
value class FeedLocalAgencyID(internal val str: String)