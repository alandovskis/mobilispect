package com.mobilispect.backend.data.schedule

interface ScheduledStopDataSource {
    fun scheduledStops(extractedDir: String, version: String): Result<Collection<ScheduledStop>>
}
