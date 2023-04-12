package com.mobilispect.backend.data.feed

interface FeedDataSource {
    fun feeds(region: String): Collection<Result<VersionedFeed>>
}
