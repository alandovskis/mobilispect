package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.agency.OneStopAgencyIDDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OneStopAgencyIDDataSourceConfiguration {
    @Bean
    fun oneStopAgencyIDDataSource(
        client: TransitLandClient,
        credentialRepository: TransitLandCredentialsRepository
    ): OneStopAgencyIDDataSource = TransitLandOneStopAgencyIDDataSource(client, credentialRepository)
}
