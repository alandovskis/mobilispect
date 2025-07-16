package com.mobilispect.backend

import java.nio.file.Path

interface ScheduledTripDataSource {
    fun trips(extractedDir: Path, version: String, feedID: String): Result<Collection<ScheduledTrip>>
}
