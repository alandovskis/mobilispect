package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.stop.StopDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class GTFSStopDataSourceConfiguration {
    @Bean
    fun stopDataSource(): StopDataSource = GTFSStopDataSource()
}