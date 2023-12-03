package com.mobilispect.backend.data.download

import java.nio.file.Path

class DownloadRequest(val url: String)

interface Downloader {
    fun download(request: DownloadRequest): Result<Path>
}
