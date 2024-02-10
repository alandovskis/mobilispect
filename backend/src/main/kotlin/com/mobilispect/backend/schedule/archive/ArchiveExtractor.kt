package com.mobilispect.backend.schedule.archive

import java.nio.file.Path

interface ArchiveExtractor {
    fun extract(archive: Path): Result<Path>
}
