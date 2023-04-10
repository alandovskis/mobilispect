package com.mobilispect.backend.data.feed

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeedDataSourceConfiguration {
    @Bean
    fun feedDataSource(): FeedDataSource = DefaultFeedDataSource()
}
