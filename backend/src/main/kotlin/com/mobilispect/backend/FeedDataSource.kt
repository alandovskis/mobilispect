package com.mobilispect.backend

import com.mobilispect.backend.schedule.feed.VersionedFeed

interface FeedDataSource {
    fun feeds(region: String): Collection<Result<VersionedFeed>>
}
