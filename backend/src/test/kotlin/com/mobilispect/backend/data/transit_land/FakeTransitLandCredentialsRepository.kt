package com.mobilispect.backend.schedule.transit_land

import com.mobilispect.backend.schedule.transit_land.api.TransitLandCredentialsRepository

class FakeTransitLandCredentialsRepository : TransitLandCredentialsRepository {
    override fun get(): String = "API_KEY"
}

