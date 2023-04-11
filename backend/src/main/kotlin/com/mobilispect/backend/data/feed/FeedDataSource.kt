package com.mobilispect.backend.data.feed

interface FeedDataSource {
    fun feeds(region: String): Result<Collection<VersionedFeed>>
}
