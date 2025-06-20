package com.mobilispect.backend.schedule.schedule

import java.nio.file.Path

interface ScheduledStopDataSource {
    fun scheduledStops(extractedDir: Path, version: String): Result<Collection<ScheduledStop>>
}
