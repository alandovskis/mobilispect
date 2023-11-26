package com.mobilispect.backend.batch

import com.mobilispect.backend.data.MongoDBInitializer
import com.mobilispect.backend.data.agency.Agency
import com.mobilispect.backend.data.agency.AgencyDataSource
import com.mobilispect.backend.data.agency.AgencyRepository
import com.mobilispect.backend.data.agency.OneStopAgencyID
import com.mobilispect.backend.data.archive.ArchiveExtractor
import com.mobilispect.backend.data.createMongoDBContainer
import com.mobilispect.backend.data.download.Downloader
import com.mobilispect.backend.data.feed.*
import com.mobilispect.backend.data.region.Region
import com.mobilispect.backend.data.region.RegionRepository
import com.mobilispect.backend.data.route.OneStopRouteID
import com.mobilispect.backend.data.route.Route
import com.mobilispect.backend.data.route.RouteDataSource
import com.mobilispect.backend.data.route.RouteRepository
import com.mobilispect.backend.data.schedule.*
import com.mobilispect.backend.data.stop.Stop
import com.mobilispect.backend.data.stop.StopDataSource
import com.mobilispect.backend.data.stop.StopRepository
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.IOException
import java.time.LocalDate

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [ImportUpdatedFeedsServiceTest.Companion.DBInitializer::class])
@Testcontainers
@Suppress("LargeClass")
@Ignore
internal class ImportUpdatedFeedsServiceTest {
    companion object {
        @Container
        @JvmStatic
        val container = createMongoDBContainer()

        class DBInitializer : MongoDBInitializer(container)
    }

    @Autowired
    lateinit var downloader: Downloader

    @Autowired
    lateinit var archiveExtractor: ArchiveExtractor

    @Autowired
    lateinit var regionRepository: RegionRepository

    @Autowired
    lateinit var feedRepository: FeedRepository

    @Autowired
    lateinit var feedVersionRepository: FeedVersionRepository

    @Autowired
    lateinit var agencyRepository: AgencyRepository

    @Autowired
    lateinit var routeRepository: RouteRepository

    @Autowired
    lateinit var stopRepository: StopRepository

    @Autowired
    lateinit var scheduledTripRepository: ScheduledTripRepository

    @Autowired
    lateinit var scheduledStopRepository: ScheduledStopRepository

    @Autowired
    lateinit var agencyDataSource: AgencyDataSource

    @Autowired
    lateinit var routeDataSource: RouteDataSource

    @Autowired
    lateinit var stopDataSource: StopDataSource

    @Autowired
    lateinit var tripDataSource: ScheduledTripDataSource

    @Autowired
    lateinit var scheduledStopDataSource: ScheduledStopDataSource

    @Test
    fun unableToRetrieveFeeds() {
        val networkDataSource = object : FeedDataSource {
            override fun feeds(region: String): Collection<Result<VersionedFeed>> =
                listOf(Result.failure(IOException("Couldn't connect")))
        }
        val subject = ImportUpdatedFeedsService(
            feedDataSource = networkDataSource,
            feedRepository = feedRepository,
            feedVersionRepository = feedVersionRepository,
            downloader = downloader,
            archiveExtractor = archiveExtractor,
            regionRepository = regionRepository,
            agencyRepository = agencyRepository,
            routeRepository = routeRepository,
            stopRepository = stopRepository,
            scheduledTripRepository = scheduledTripRepository,
            scheduledStopRepository = scheduledStopRepository,
            agencyDataSource = agencyDataSource,
            routeDataSource = routeDataSource,
            stopDataSource = stopDataSource,
            scheduledTripDataSource = tripDataSource,
            scheduledStopDataSource = scheduledStopDataSource,
        )

        subject.get()

        assertThat(feedRepository.findAll()).isEmpty()
        assertThat(feedVersionRepository.findAll()).isEmpty()
        assertThat(agencyRepository.findAll()).isEmpty()
        assertThat(routeRepository.findAll()).isEmpty()
        assertThat(stopRepository.findAll()).isEmpty()
        assertThat(scheduledTripRepository.findAll()).isEmpty()
        assertThat(scheduledStopRepository.findAll()).isEmpty()
    }

    @Test
    fun unableToDownloadFeed() {
        val mockServer = MockWebServer()
        mockServer.enqueue(MockResponse())
        mockServer.start()

        val networkDataSource = object : FeedDataSource {
            override fun feeds(region: String): Collection<Result<VersionedFeed>> =
                listOf(
                    Result.success(
                        VersionedFeed(
                            feed = Feed(
                                _id = "f-f256-exo~citlapresquîle",
                                url = mockServer.url("").toString()
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
        mockServer.shutdown()
        val subject = ImportUpdatedFeedsService(
            feedDataSource = networkDataSource,
            feedRepository = feedRepository,
            feedVersionRepository = feedVersionRepository,
            downloader = downloader,
            archiveExtractor = archiveExtractor,
            regionRepository = regionRepository,
            agencyRepository = agencyRepository,
            routeRepository = routeRepository,
            stopRepository = stopRepository,
            scheduledTripRepository = scheduledTripRepository,
            scheduledStopRepository = scheduledStopRepository,
            agencyDataSource = agencyDataSource,
            routeDataSource = routeDataSource,
            stopDataSource = stopDataSource,
            scheduledTripDataSource = tripDataSource,
            scheduledStopDataSource = scheduledStopDataSource,
        )

        subject.get()

        assertThat(feedRepository.findAll()).isEmpty()
        assertThat(feedVersionRepository.findAll()).isEmpty()
        assertThat(agencyRepository.findAll()).isEmpty()
        assertThat(routeRepository.findAll()).isEmpty()
        assertThat(stopRepository.findAll()).isEmpty()
        assertThat(scheduledTripRepository.findAll()).isEmpty()
        assertThat(scheduledStopRepository.findAll()).isEmpty()
    }

    @Test
    fun addsFeedAndVersionIfNone() {
        val region = Region(_id = "reg-f25-mtl", name = "Montréal")
        regionRepository.save(region)

        val feedDataSource = object : FeedDataSource {
            override fun feeds(region: String): Collection<Result<VersionedFeed>> =
                listOf(
                    Result.success(
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

        val version = "d89aa5de884111e4b6a9365220ded9f746ef2dbf"
        val subject = ImportUpdatedFeedsService(
            feedDataSource = feedDataSource,
            feedRepository = feedRepository,
            feedVersionRepository = feedVersionRepository,
            downloader = downloader,
            archiveExtractor = archiveExtractor,
            regionRepository = regionRepository,
            agencyRepository = agencyRepository,
            routeRepository = routeRepository,
            stopRepository = stopRepository,
            scheduledTripRepository = scheduledTripRepository,
            scheduledStopRepository = scheduledStopRepository,
            agencyDataSource = agencyDataSource,
            routeDataSource = routeDataSource,
            stopDataSource = stopDataSource,
            scheduledTripDataSource = tripDataSource,
            scheduledStopDataSource = scheduledStopDataSource,
        )

        subject.get()

        importedAllFeeds()
        importedAllAgencies(version)
        importedAllRoutes(version)
        importedAllStops(version)
        importedAllTrips(version)
        importedAllStopTimes(version)
    }

    private fun importedAllFeeds() {
        val actualFeeds = feedRepository.findAll()
        assertThat(actualFeeds).contains(
            Feed(
                _id = "f-f256-exo~citlapresquîle",
                url = "https://exo.quebec/xdata/citpi/google_transit.zip"
            )
        )

        val actualFeedVersions = feedVersionRepository.findAll()
        assertThat(actualFeedVersions).contains(
            FeedVersion(
                _id = "d89aa5de884111e4b6a9365220ded9f746ef2dbf",
                feedID = "f-f256-exo~citlapresquîle",
                startsOn = LocalDate.of(2022, 11, 23),
                endsOn = LocalDate.of(2023, 6, 25)
            )
        )
    }

    private fun importedAllAgencies(version: String) {
        val agencies = agencyRepository.findAll()
        assertThat(agencies).contains(
            Agency(id = OneStopAgencyID("o-f256-exo~citlapresquîle"), name = "exo-La Presqu'île", version = version)
        )
    }

    private fun importedAllRoutes(version: String) {
        val routes = routeRepository.findAll()
        assertThat(routes).contains(
            Route(
                id = OneStopRouteID("r-f2566-1"),
                shortName = "1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = OneStopAgencyID("o-f256-exo~citlapresquîle"),
                version = version
            )
        )
        assertThat(routes).contains(
            Route(
                id = OneStopRouteID("r-f2566-t1"),
                shortName = "T1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = OneStopAgencyID("o-f256-exo~citlapresquîle"),
                version = version
            )
        )
    }

    private fun importedAllStops(version: String) {
        val stops = stopRepository.findAll()
        assertThat(stops).contains(
            Stop(
                _id = "s-f256hrvf2g-1eboulevard~11eavenue",
                name = "1e Boulevard / 11e Avenue",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "s-f256hrtws3-1eboulevard~11eavenue",
                name = "1e Boulevard / 11e Avenue",
                version = version
            )
        )
    }

    @Suppress("LongMethod")
    private fun importedAllTrips(version: String) {
        val trips = scheduledTripRepository.findAll()

        assertThat(trips).contains(
            ScheduledTrip(
                _id = "3281905-PI-A22-PI_GTFS-Semaine-01",
                routeID = OneStopRouteID("r-f2566-1"),
                direction = "Seigneurie - Joseph-Carrier AM",
                version = version,
                dates = listOf(
                    LocalDate.of(2022, 11, 23),
                    LocalDate.of(2022, 11, 24),
                    LocalDate.of(2022, 11, 25),

                    LocalDate.of(2022, 11, 28),
                    LocalDate.of(2022, 11, 29),
                    LocalDate.of(2022, 11, 30),
                    LocalDate.of(2022, 12, 1),
                    LocalDate.of(2022, 12, 2),

                    LocalDate.of(2022, 12, 5),
                    LocalDate.of(2022, 12, 6),
                    LocalDate.of(2022, 12, 7),
                    LocalDate.of(2022, 12, 8),
                    LocalDate.of(2022, 12, 9),

                    LocalDate.of(2022, 12, 12),
                    LocalDate.of(2022, 12, 13),
                    LocalDate.of(2022, 12, 14),
                    LocalDate.of(2022, 12, 15),
                    LocalDate.of(2022, 12, 16),

                    LocalDate.of(2022, 12, 19),
                    LocalDate.of(2022, 12, 20),
                    LocalDate.of(2022, 12, 21),
                    LocalDate.of(2022, 12, 22),
                    LocalDate.of(2022, 12, 23),
                )
            )
        )

        assertThat(trips).contains(
            ScheduledTrip(
                _id = "3282456-PI-A22-PI_GTFS-Fête-1-01",
                routeID = OneStopRouteID("r-f2565-115"),
                direction = "Terminus Vaudreuil",
                version = version,
                dates = listOf(
                    LocalDate.of(2022, 12, 26),
                    LocalDate.of(2023, 1, 2)
                )
            )
        )
        assertThat(trips).contains(
            ScheduledTrip(
                _id = "3282393-PI-A22-PI_GTFS-Samedi-01",
                routeID = OneStopRouteID("r-f2565-115"),
                direction = "Terminus Vaudreuil",
                version = version,
                dates = listOf(
                    LocalDate.of(2022, 11, 26),
                    LocalDate.of(2022, 12, 3),
                    LocalDate.of(2022, 12, 10),
                    LocalDate.of(2022, 12, 17),
                    LocalDate.of(2022, 12, 24),
                    LocalDate.of(2022, 12, 31),
                    LocalDate.of(2023, 1, 7),
                )
            )
        )
        assertThat(trips).contains(
            ScheduledTrip(
                _id = "3282456-PI-A22-PI_GTFS-Dimanche-01",
                routeID = OneStopRouteID("r-f2565-115"),
                direction = "Terminus Vaudreuil",
                version = version,
                dates = listOf(
                    LocalDate.of(2022, 11, 27),
                    LocalDate.of(2022, 12, 4),
                    LocalDate.of(2022, 12, 11),
                    LocalDate.of(2022, 12, 18),
                    LocalDate.of(2022, 12, 25),
                    LocalDate.of(2023, 1, 1),
                    LocalDate.of(2023, 1, 8),
                )
            )
        )
    }

    @Suppress("LongMethod")
    private fun importedAllStopTimes(version: String) {
        val scheduledStops = scheduledStopRepository.findAll()
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72986",
                departsAt = DateTimeOffset(hours = 6, minutes = 37),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 37),
                stopSequence = 1,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72748",
                departsAt = DateTimeOffset(hours = 6, minutes = 37, seconds = 28),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 37, seconds = 28),
                stopSequence = 2,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72960",
                departsAt = DateTimeOffset(hours = 6, minutes = 37, seconds = 38),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 37, seconds = 38),
                stopSequence = 3,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72711",
                departsAt = DateTimeOffset(hours = 6, minutes = 37, seconds = 51),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 37, seconds = 51),
                stopSequence = 4,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72618",
                departsAt = DateTimeOffset(hours = 6, minutes = 38, seconds = 0),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 38, seconds = 0),
                stopSequence = 5,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72828",
                departsAt = DateTimeOffset(hours = 6, minutes = 42, seconds = 0),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 42, seconds = 0),
                stopSequence = 6,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72698",
                departsAt = DateTimeOffset(hours = 6, minutes = 42, seconds = 26),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 42, seconds = 26),
                stopSequence = 7,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72702",
                departsAt = DateTimeOffset(hours = 6, minutes = 42, seconds = 55),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 42, seconds = 55),
                stopSequence = 8,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72704",
                departsAt = DateTimeOffset(hours = 6, minutes = 43, seconds = 26),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 43, seconds = 26),
                stopSequence = 9,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72696",
                departsAt = DateTimeOffset(hours = 6, minutes = 43, seconds = 57),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 43, seconds = 57),
                stopSequence = 10,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72705",
                departsAt = DateTimeOffset(hours = 6, minutes = 44, seconds = 15),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 44, seconds = 15),
                stopSequence = 11,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72707",
                departsAt = DateTimeOffset(hours = 6, minutes = 44, seconds = 55),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 44, seconds = 55),
                stopSequence = 12,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72710",
                departsAt = DateTimeOffset(hours = 6, minutes = 45, seconds = 42),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 45, seconds = 42),
                stopSequence = 13,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72916",
                departsAt = DateTimeOffset(hours = 6, minutes = 46, seconds = 4),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 46, seconds = 4),
                stopSequence = 14,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72901",
                departsAt = DateTimeOffset(hours = 6, minutes = 47, seconds = 25),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 47, seconds = 25),
                stopSequence = 15,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72577",
                departsAt = DateTimeOffset(hours = 6, minutes = 48, seconds = 0),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 48, seconds = 0),
                stopSequence = 16,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72579",
                departsAt = DateTimeOffset(hours = 6, minutes = 48, seconds = 11),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 48, seconds = 11),
                stopSequence = 17,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72581",
                departsAt = DateTimeOffset(hours = 6, minutes = 48, seconds = 36),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 48, seconds = 36),
                stopSequence = 18,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72567",
                departsAt = DateTimeOffset(hours = 6, minutes = 49, seconds = 1),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 49, seconds = 1),
                stopSequence = 19,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72568",
                departsAt = DateTimeOffset(hours = 6, minutes = 49, seconds = 30),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 49, seconds = 30),
                stopSequence = 20,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72570",
                departsAt = DateTimeOffset(hours = 6, minutes = 49, seconds = 42),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 49, seconds = 42),
                stopSequence = 21,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72626",
                departsAt = DateTimeOffset(hours = 6, minutes = 50, seconds = 8),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 50, seconds = 8),
                stopSequence = 22,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72722",
                departsAt = DateTimeOffset(hours = 6, minutes = 50, seconds = 36),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 50, seconds = 36),
                stopSequence = 23,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72720",
                departsAt = DateTimeOffset(hours = 6, minutes = 51, seconds = 6),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 51, seconds = 6),
                stopSequence = 24,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72789",
                departsAt = DateTimeOffset(hours = 6, minutes = 51, seconds = 26),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 51, seconds = 26),
                stopSequence = 25,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72571",
                departsAt = DateTimeOffset(hours = 6, minutes = 52, seconds = 0),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 52, seconds = 0),
                stopSequence = 26,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72919",
                departsAt = DateTimeOffset(hours = 6, minutes = 52, seconds = 20),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 52, seconds = 20),
                stopSequence = 27,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72801",
                departsAt = DateTimeOffset(hours = 6, minutes = 52, seconds = 35),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 52, seconds = 35),
                stopSequence = 28,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72585",
                departsAt = DateTimeOffset(hours = 6, minutes = 52, seconds = 54),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 52, seconds = 54),
                stopSequence = 29,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72819",
                departsAt = DateTimeOffset(hours = 6, minutes = 53, seconds = 48),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 53, seconds = 48),
                stopSequence = 30,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72728",
                departsAt = DateTimeOffset(hours = 6, minutes = 54, seconds = 57),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 54, seconds = 57),
                stopSequence = 31,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72825",
                departsAt = DateTimeOffset(hours = 6, minutes = 55, seconds = 23),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 55, seconds = 23),
                stopSequence = 32,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72968",
                departsAt = DateTimeOffset(hours = 6, minutes = 56, seconds = 7),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 56, seconds = 7),
                stopSequence = 33,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72914",
                departsAt = DateTimeOffset(hours = 6, minutes = 57, seconds = 33),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 57, seconds = 33),
                stopSequence = 34,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72617",
                departsAt = DateTimeOffset(hours = 6, minutes = 58, seconds = 0),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 58, seconds = 0),
                stopSequence = 35,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72712",
                departsAt = DateTimeOffset(hours = 6, minutes = 58, seconds = 59),
                arrivesAt = DateTimeOffset(hours = 6, minutes = 58, seconds = 59),
                stopSequence = 36,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72961",
                departsAt = DateTimeOffset(hours = 7, minutes = 0, seconds = 19),
                arrivesAt = DateTimeOffset(hours = 7, minutes = 0, seconds = 19),
                stopSequence = 37,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72750",
                departsAt = DateTimeOffset(hours = 7, minutes = 1, seconds = 3),
                arrivesAt = DateTimeOffset(hours = 7, minutes = 1, seconds = 3),
                stopSequence = 38,
                version = version
            )
        )
        assertThat(scheduledStops).contains(
            ScheduledStop(
                tripID = "3281905-PI-A22-PI_GTFS-Semaine-01",
                stopID = "72986",
                departsAt = DateTimeOffset(hours = 7, minutes = 3, seconds = 0),
                arrivesAt = DateTimeOffset(hours = 7, minutes = 3, seconds = 0),
                stopSequence = 39,
                version = version
            )
        )
    }

    @AfterEach
    fun clear() {
        regionRepository.deleteAll()
        scheduledStopRepository.deleteAll()
        scheduledTripRepository.deleteAll()
        stopRepository.deleteAll()
        routeRepository.deleteAll()
        agencyRepository.deleteAll()
        feedVersionRepository.deleteAll()
        feedRepository.deleteAll()
    }
}
