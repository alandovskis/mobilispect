package com.mobilispect.backend.schedule.transit_land.internal.credentials

import com.mobilispect.backend.schedule.transit_land.api.TransitLandCredentialsRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Repository

/**
 * A [TransitLandCredentialsRepository] that uses a properties as its source.
 */
@PropertySource("classpath:transit-land.properties")
@Repository
class PropertiesTransitLandCredentialsRepository : TransitLandCredentialsRepository {

    @Value("\${transitLand.apiKey:none}")
    lateinit var apiKey: String

    override fun get(): String? = apiKey
}
