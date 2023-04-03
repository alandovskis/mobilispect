package com.mobilispect.backend.data.agency

interface AgencyDataSource {
    fun agencies(root: String, version: String): Result<Collection<Agency>>
}
