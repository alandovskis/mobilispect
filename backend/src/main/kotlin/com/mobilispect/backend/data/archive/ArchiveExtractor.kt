package com.mobilispect.backend.data.archive

interface ArchiveExtractor {
    fun extract(archive: String): Result<String>
}
