package com.mobilispect.data.transit_land

import com.mobilispect.data.asset.AssetProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

interface TransitLandConfigRepository {
    fun config(): TransitLandConfig?
}

@OptIn(ExperimentalSerializationApi::class)
class DefaultTransitLandConfigRepository @Inject constructor(private val assetProvider: AssetProvider) :
    TransitLandConfigRepository {
    override fun config(): TransitLandConfig? {
        val configFile = assetProvider.asset(FILE_NAME).getOrNull() ?: return null
        return Json.decodeFromStream<TransitLandConfig>(configFile)
    }

    companion object {
        const val FILE_NAME = "transit-land-config.json"
    }
}