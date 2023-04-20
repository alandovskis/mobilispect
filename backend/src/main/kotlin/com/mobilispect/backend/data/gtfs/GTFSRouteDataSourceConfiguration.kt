package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.agency.OneStopAgencyIDDataSource
import com.mobilispect.backend.data.route.OneStopRouteIDDataSource
import com.mobilispect.backend.data.route.RouteDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class GTFSRouteDataSourceConfiguration {
    @Bean
    fun routeDataSource(
        agencyIDDataSource: OneStopAgencyIDDataSource,
        routeIDDataSource: OneStopRouteIDDataSource
    ): RouteDataSource = GTFSRouteDataSource(agencyIDDataSource, routeIDDataSource)
}
