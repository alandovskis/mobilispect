package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.schedule.agency.AgencyIDDataSource
import com.mobilispect.backend.schedule.route.RouteDataSource
import com.mobilispect.backend.schedule.route.RouteIDDataSource
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
