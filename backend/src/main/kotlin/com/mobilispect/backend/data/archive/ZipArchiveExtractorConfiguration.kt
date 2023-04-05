package com.mobilispect.backend.data.archive

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ZipArchiveExtractorConfiguration {
    @Bean
    fun extractor(): ArchiveExtractor = ZipArchiveExtractor()
}