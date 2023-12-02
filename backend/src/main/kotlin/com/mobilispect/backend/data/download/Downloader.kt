package com.mobilispect.backend.data.download

import java.nio.file.Path

interface Downloader {
    fun download(url: String): Result<Path>
}
