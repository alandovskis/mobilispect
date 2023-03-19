package com.mobilispect.backend.data.transit_land

class FakeTransitLandCredentialsRepository : TransitLandCredentialsRepository {
    override fun get(): String = "API_KEY"
}