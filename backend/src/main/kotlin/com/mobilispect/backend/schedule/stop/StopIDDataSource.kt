package com.mobilispect.backend.schedule.stop

interface StopIDDataSource {
    fun stops(feedID: String): Result<Map<String, String>>
}
