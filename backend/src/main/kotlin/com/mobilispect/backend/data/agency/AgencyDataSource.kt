package com.mobilispect.backend.data.agency

import java.nio.file.Path

interface AgencyDataSource {
    fun agencies(root: Path, version: String, feedID: String): Result<Collection<Agency>>
}
