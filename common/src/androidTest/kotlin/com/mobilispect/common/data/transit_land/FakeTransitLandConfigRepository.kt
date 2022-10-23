package com.mobilispect.common.data.transit_land

class FakeTransitLandConfigRepository : TransitLandConfigRepository {
    private var config: TransitLandConfig? = null

    override fun config(): TransitLandConfig? = config

    fun insert(config: TransitLandConfig) {
        this.config = config
    }

}
