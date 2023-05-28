package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.transit_land.api.TransitLandCredentialsRepository

class FakeTransitLandCredentialsRepository : TransitLandCredentialsRepository {
    override fun get(): String = "API_KEY"
}

