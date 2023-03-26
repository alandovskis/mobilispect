package com.mobilispect.backend.data.feed

import java.time.LocalDate

class DefaultFeedDataSource : FeedDataSource {
    override fun feeds(): Result<Collection<VersionedFeed>> = Result.success(
        listOf(
            /*VersionedFeed(
                feed = Feed(
                    _id = "f-f25d-socitdetransportdemontral",
                    name = "Société de transport de Montréal (STM)",
                    url = "https://www.stm.info/sites/default/files/gtfs/gtfs_stm.zip"
                ),
                version = FeedVersion(
                    _id = "f41195513247d422c278811c9fb4b55db40cf210",
                    feedID = "f-f25d-socitdetransportdemontral",
                    startsOn = LocalDate.of(2023, 1, 9),
                    endsOn = LocalDate.of(2023, 6, 18)
                )
            )*/
            VersionedFeed(
                feed = Feed(
                    _id = "f-f256-exo~citlapresquîle",
                    name = "exo la Presqu'ile",
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