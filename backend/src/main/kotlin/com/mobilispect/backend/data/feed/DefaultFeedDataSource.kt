package com.mobilispect.backend.data.feed

import java.time.LocalDate

class DefaultFeedDataSource : FeedDataSource {
    override fun feeds(): Result<Collection<VersionedFeed>> = Result.success(
        listOf(
            VersionedFeed(
                feed = Feed(
                    _id = "f-f25d-socitdetransportdemontral",
                    name = "Société de transport de Montréal (STM)",
                    url = "https://www.stm.info/sites/default/files/gtfs/gtfs_stm.zip"
                ),
                version = FeedVersion(
                    feedID = "f-f25d-socitdetransportdemontral",
                    version = "f41195513247d422c278811c9fb4b55db40cf210",
                    startsOn = LocalDate.of(2023, 1, 9),
                    endsOn = LocalDate.of(2023, 6, 18)
                )
            )
        )
    )
}