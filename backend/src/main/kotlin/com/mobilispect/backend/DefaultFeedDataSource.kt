package com.mobilispect.backend.schedule.feed

import com.mobilispect.backend.FeedDataSource
import com.mobilispect.backend.FeedVersion
import java.time.LocalDate

@Suppress("MagicNumber")
class DefaultFeedDataSource : FeedDataSource {
    override fun feeds(region: String): Collection<Result<VersionedFeed>> =
        listOf(
            Result.success(
                VersionedFeed(
                    feed = Feed(
                        _id = "f-f256-exo~citlapresquîle",
                        url = "https://exo.quebec/xdata/citpi/google_transit.zip"
                    ),
                    version = FeedVersion(
                        uid = "a2b4c6",
                        feedID = "f-f256-exo~citlapresquîle" ,
                        startsOn = LocalDate.of(2022, 11, 23),
                        endsOn = LocalDate.of(2023, 6, 25)
                    )
            )
        )
    )
}
