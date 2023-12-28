package com.mobilispect.backend.data.archive

import java.nio.file.Path

interface ArchiveExtractor {
    fun extract(archive: Path): Result<Path>
}
