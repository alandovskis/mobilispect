package com.mobilispect.backend.schedule.feed

import com.mobilispect.backend.Feed
import com.mobilispect.backend.FeedDataSource
import com.mobilispect.backend.FeedVersion
import com.mobilispect.backend.schedule.ScheduledFeed
import java.time.LocalDate

@Suppress("MagicNumber")
class DefaultFeedDataSource : FeedDataSource {
    override fun feeds(region: String): Collection<Result<ScheduledFeed>> =
        listOf(
            Result.success(
                ScheduledFeed(
                    feed = Feed(
                        uid = "f-f256-exo~citlapresquîle",
                        url = "https://exo.quebec/xdata/citpi/google_transit.zip"
                    ),
                    version = FeedVersion(
                        uid = "a2b4c6",
                        feedID = "f-f256-exo~citlapresquîle",
                        startsOn = LocalDate.of(2022, 11, 23),
                        endsOn = LocalDate.of(2023, 6, 25)
                    )
                )
        )
    )
}
