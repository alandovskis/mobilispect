package com.mobilispect.backend.schedule.route

import java.nio.file.Path

interface RouteDataSource {
    fun routes(root: Path, version: String, feedID: String): Result<Collection<Route>>
}
