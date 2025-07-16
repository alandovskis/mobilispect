package com.mobilispect.backend

interface StopIDDataSource {
    fun stops(feedID: String): Result<Map<String, String>>
}
