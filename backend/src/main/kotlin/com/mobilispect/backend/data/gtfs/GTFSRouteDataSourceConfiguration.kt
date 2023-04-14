package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.route.RouteDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class GTFSRouteDataSourceConfiguration {
    @Bean
    fun routeDataSource(): RouteDataSource = GTFSRouteDataSource()
}
