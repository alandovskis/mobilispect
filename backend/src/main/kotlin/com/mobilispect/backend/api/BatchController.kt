package com.mobilispect.backend.api

import com.mobilispect.backend.batch.ImportUpdatedFeedsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BatchController(
    private val importUpdatedFeedsService: ImportUpdatedFeedsService
) {
    @PostMapping("/batch/import/feeds")
    fun importUpdatedFeeds() {
        importUpdatedFeedsService.get()
    }
}