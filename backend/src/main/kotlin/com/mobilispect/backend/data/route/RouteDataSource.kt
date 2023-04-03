package com.mobilispect.backend.data.route

interface RouteDataSource {
    fun routes(root: String, version: String): Result<Collection<Route>>
}