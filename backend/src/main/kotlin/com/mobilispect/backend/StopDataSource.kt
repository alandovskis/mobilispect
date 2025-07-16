package com.mobilispect.backend.schedule.stop

import com.mobilispect.backend.Stop
import java.nio.file.Path

interface StopDataSource {
    fun stops(root: Path, version: String, feedID: String): Result<Collection<Stop>>
}
