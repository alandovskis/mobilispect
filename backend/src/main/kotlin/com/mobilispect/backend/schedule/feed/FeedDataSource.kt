package com.mobilispect.backend.schedule.feed

interface FeedDataSource {
    fun feeds(region: String): Collection<Result<VersionedFeed>>
}
