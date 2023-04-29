package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.agency.AgencyIDDataSource
import com.mobilispect.backend.data.route.RouteDataSource
import com.mobilispect.backend.data.route.RouteIDDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class GTFSRouteDataSourceConfiguration {
    @Bean
    fun routeDataSource(
        agencyIDDataSource: AgencyIDDataSource,
        routeIDDataSource: RouteIDDataSource
    ): RouteDataSource = GTFSRouteDataSource(agencyIDDataSource, routeIDDataSource)
}
