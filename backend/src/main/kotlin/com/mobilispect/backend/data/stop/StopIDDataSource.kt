package com.mobilispect.backend.data.stop

interface StopIDDataSource {
    fun stops(feedID: String): Result<Map<String, String>>
}