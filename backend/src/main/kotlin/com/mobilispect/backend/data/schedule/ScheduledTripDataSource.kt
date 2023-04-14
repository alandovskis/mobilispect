package com.mobilispect.backend.data.schedule

interface ScheduledTripDataSource {
    fun trips(extractedDir: String, version: String): Result<Collection<ScheduledTrip>>
}
