package com.mobilispect.backend.schedule.agency

/**
 * An agency ID that is only unique within a feed.
 */
@JvmInline
value class FeedLocalAgencyID(internal val str: String)
