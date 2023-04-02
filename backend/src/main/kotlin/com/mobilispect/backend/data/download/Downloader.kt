package com.mobilispect.backend.data.download

interface Downloader {
    fun download(url: String): Result<String>
}
