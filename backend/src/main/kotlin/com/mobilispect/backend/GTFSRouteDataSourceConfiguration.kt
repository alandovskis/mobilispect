package com.mobilispect.backend

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
