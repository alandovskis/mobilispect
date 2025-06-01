package com.mobilispect.mobile.data.transit_land

import javax.inject.Inject

interface TransitLandConfigRepository {
    fun config(): TransitLandConfig?
}

class DefaultTransitLandConfigRepository @Inject constructor() :
    TransitLandConfigRepository {
    override fun config(): TransitLandConfig? {
        return TransitLandConfig(
            apiKey = "CvaXEwfuCJ7x1mC169r53ygiVU8N55mj"
        )
    }

    companion object {
        const val FILE_NAME = "transit-land-config.json"
    }
}