package com.mobilispect.backend

interface StopIDDataSource {
    fun stop(feedID: String, stopID: String): Result<String>
}
