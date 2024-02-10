package com.mobilispect.backend.api

import com.mobilispect.backend.schedule.ImportScheduledFeedsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BatchController(
    private val importScheduledFeedsService: ImportScheduledFeedsService
) {
    @PostMapping("/batch/import/feeds")
    fun importUpdatedFeeds() {
        importScheduledFeedsService.get()
    }
}
