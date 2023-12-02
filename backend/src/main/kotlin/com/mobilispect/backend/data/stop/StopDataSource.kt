package com.mobilispect.backend.data.stop

import java.nio.file.Path

interface StopDataSource {
    fun stops(root: Path, version: String, feedID: String): Result<Collection<Stop>>
}
