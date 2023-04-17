package com.mobilispect.backend.data.agency

interface AgencyDataSource {
    fun agencies(root: String, version: String, feedID: String): Result<Collection<Agency>>
}
