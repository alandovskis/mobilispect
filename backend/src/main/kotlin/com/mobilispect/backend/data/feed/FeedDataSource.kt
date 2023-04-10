package com.mobilispect.backend.data.feed

interface FeedDataSource {
    fun feeds(): Result<Collection<VersionedFeed>>
}
