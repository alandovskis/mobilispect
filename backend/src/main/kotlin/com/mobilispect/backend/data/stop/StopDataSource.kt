package com.mobilispect.backend.data.stop

interface StopDataSource {
    fun stops(root: String, version: String, feedID: String): Result<Collection<Stop>>
}
