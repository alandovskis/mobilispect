package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.feed.Feed
import com.mobilispect.backend.data.feed.FeedVersion
import com.mobilispect.backend.data.feed.VersionedFeed
import java.time.LocalDate

val TRANSIT_LAND_FEED_FIXTURE = VersionedFeed(
    feed = Feed(
        _id = "f-f25f-rseaudetransportdelongueuil",
        url = "https://www.rtl-longueuil.qc.ca/transit/latestfeed/RTL.zip"
    ),
    version = FeedVersion(
        _id = "41c3e41b979db2e58f9deeb98f8f91be47f3ba17",
        feedID = "f-f25f-rseaudetransportdelongueuil",
        startsOn = LocalDate.of(2023, 6, 26),
        endsOn = LocalDate.of(2023, 8, 20)
    )
)

