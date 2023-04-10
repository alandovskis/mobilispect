package com.mobilispect.backend.data.feed

import java.time.LocalDate

@Suppress("MagicNumber")
class DefaultFeedDataSource : FeedDataSource {
    override fun feeds(): Result<Collection<VersionedFeed>> = Result.success(
        listOf(
            VersionedFeed(
                feed = Feed(
                    _id = "f-f256-exo~citlapresquîle",
                    url = "https://exo.quebec/xdata/citpi/google_transit.zip"
                ),
                version = FeedVersion(
                    _id = "d89aa5de884111e4b6a9365220ded9f746ef2dbf",
                    feedID = "f-f256-exo~citlapresquîle",
                    startsOn = LocalDate.of(2022, 11, 23),
                    endsOn = LocalDate.of(2023, 6, 25)
                )
            )
        )
    )
}
