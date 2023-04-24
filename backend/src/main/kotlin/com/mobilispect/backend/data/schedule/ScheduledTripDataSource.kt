package com.mobilispect.backend.data.schedule

interface ScheduledTripDataSource {
    fun trips(extractedDir: String, version: String, feedID: String): Result<Collection<ScheduledTrip>>
}
