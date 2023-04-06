package com.mobilispect.backend.batch

import com.mobilispect.backend.data.MongoDBInitializer
import com.mobilispect.backend.data.agency.Agency
import com.mobilispect.backend.data.agency.AgencyDataSource
import com.mobilispect.backend.data.agency.AgencyRepository
import com.mobilispect.backend.data.archive.ArchiveExtractor
import com.mobilispect.backend.data.createMongoDBContainer
import com.mobilispect.backend.data.download.Downloader
import com.mobilispect.backend.data.feed.DefaultFeedDataSource
import com.mobilispect.backend.data.feed.Feed
import com.mobilispect.backend.data.feed.FeedDataSource
import com.mobilispect.backend.data.feed.FeedRepository
import com.mobilispect.backend.data.feed.FeedVersion
import com.mobilispect.backend.data.feed.FeedVersionRepository
import com.mobilispect.backend.data.feed.VersionedFeed
import com.mobilispect.backend.data.route.Route
import com.mobilispect.backend.data.route.RouteDataSource
import com.mobilispect.backend.data.route.RouteRepository
import com.mobilispect.backend.data.schedule.DateTimeOffset
import com.mobilispect.backend.data.schedule.ScheduledStop
import com.mobilispect.backend.data.schedule.ScheduledStopRepository
import com.mobilispect.backend.data.schedule.ScheduledTrip
import com.mobilispect.backend.data.schedule.ScheduledTripDataSource
import com.mobilispect.backend.data.schedule.ScheduledTripRepository
import com.mobilispect.backend.data.stop.Stop
import com.mobilispect.backend.data.stop.StopDataSource
import com.mobilispect.backend.data.stop.StopRepository
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [ImportUpdatedFeedsServiceTest.Companion.DBInitializer::class])
@Testcontainers
class ImportUpdatedFeedsServiceTest {
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

    @Test
    fun noNetwork() {
        val mockServer = MockWebServer()
        mockServer.enqueue(MockResponse())
        mockServer.start()

        val networkDataSource = object : FeedDataSource {
            override fun feeds(): Result<Collection<VersionedFeed>> =
                Result.success(
                    listOf(
                        VersionedFeed(
                            feed = Feed(
                                _id = "f-f256-exo~citlapresquîle",
                                name = "exo la Presqu'ile",
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
            agencyRepository = agencyRepository,
            routeRepository = routeRepository,
            stopRepository = stopRepository,
            scheduledTripRepository = scheduledTripRepository,
            scheduledStopRepository = scheduledStopRepository,
            agencyDataSource = agencyDataSource,
            routeDataSource = routeDataSource,
            stopDataSource = stopDataSource,
            scheduledTripDataSource = tripDataSource
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
        val networkDataSource = DefaultFeedDataSource()

        val expected = networkDataSource.feeds().getOrNull()!!
        val subject = ImportUpdatedFeedsService(
            feedDataSource = networkDataSource,
            feedRepository = feedRepository,
            feedVersionRepository = feedVersionRepository,
            downloader = downloader,
            archiveExtractor = archiveExtractor,
            agencyRepository = agencyRepository,
            routeRepository = routeRepository,
            stopRepository = stopRepository,
            scheduledTripRepository = scheduledTripRepository,
            scheduledStopRepository = scheduledStopRepository,
            agencyDataSource = agencyDataSource,
            routeDataSource = routeDataSource,
            stopDataSource = stopDataSource,
            scheduledTripDataSource = tripDataSource
        )

        subject.get()

        importedAllFeeds(expected)
        importedAllAgencies(expected.first().version._id)
        importedAllRoutes(expected.first().version._id)
        importedAllStops(expected.first().version._id)
        importedAllTrips(expected.first().version._id)
        importedAllStopTimes(expected.first().version._id)
    }

    private fun importedAllFeeds(expected: Collection<VersionedFeed>) {
        val actualFeeds = feedRepository.findAll()
        assertThat(actualFeeds).containsAll(expected.map { feed -> feed.feed })
        val actualFeedVersions = feedVersionRepository.findAll()
        assertThat(actualFeedVersions).containsAll(expected.map { feed -> feed.version })
    }

    private fun importedAllAgencies(version: String) {
        val agencies = agencyRepository.findAll()
        assertThat(agencies).contains(
            Agency(_id = "CITPI", name = "exo-La Presqu'île", version = version)
        )
    }

    private fun importedAllRoutes(version: String) {
        val routes = routeRepository.findAll()
        assertThat(routes).contains(
            Route(
                _id = "1",
                shortName = "1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "10",
                shortName = "10",
                longName = "Gare Vaudreuil/Auto-Plaza-Brunswick",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "115",
                shortName = "115",
                longName = "Gare Vaudreuil / Dorion (Av. de la Fabrique)",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "15",
                shortName = "15",
                longName = "Gare Vaudreuil / Gare Dorion via Valois  P-G-L",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "2",
                shortName = "2",
                longName = "Gare Vaudreuil/Bourget/des Sarcelles",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "21",
                shortName = "21",
                longName = "Gare Vaudreuil/Hudson/St-Charles",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "3",
                shortName = "3",
                longName = "Gare Dorion/Valois/Tonnancour",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "31",
                shortName = "31",
                longName = "Gare Pincourt T-V/Cardinal-Léger",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "33",
                shortName = "33",
                longName = "Gare Pincourt T-V/Forest",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "335",
                shortName = "335",
                longName = "Gare Pincourt T-V/Ste-Anne-de-Bellevue",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "35",
                shortName = "35",
                longName = "Dorion / Ste-Anne-de-Bellevue via l'Ile Perrot",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "4",
                shortName = "4",
                longName = "Gare Vaudreuil/Ouimet/Floralies",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "40",
                shortName = "40",
                longName = "Express Vaudreuil - Terminus Côte-Vertu",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "41",
                shortName = "41",
                longName = "Gare Île-Perrot/Boishatel/Perrier/F. Cuillerier",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "42",
                shortName = "42",
                longName = "Gare Île-Perrot / des Érables / Lucien-Manning",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "43",
                shortName = "43",
                longName = "Gare Île-Perrot/Perrot/Rivelaine",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "44",
                shortName = "44",
                longName = "Gares Pincourt/T-V Ile-Perrot / John-Abbott",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "45",
                shortName = "45",
                longName = "Virginie-Roy / Ste-Anne-De-Bellevue",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "46",
                shortName = "46",
                longName = "Gare Île-Perrot/Don Quichotte/Pointe-du-Domaine",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "47",
                shortName = "47",
                longName = "Perrot/Caza/Pointe-du-Domaine",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "5",
                shortName = "5",
                longName = "Gare Vaudreuil/Gare Dorion",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "51",
                shortName = "51",
                longName = "Gare Vaudreuil / Saint-Lazare",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "6",
                shortName = "6",
                longName = "Gare Vaudreuil/CSSS/du Ruisselet/Émile-Bouchard",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "61",
                shortName = "61",
                longName = "Gare Vaudreuil/Rigaud",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "7",
                shortName = "7",
                longName = "Gare Vaudreuil/John Abbott/Pointe-Claire",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "8",
                shortName = "8",
                longName = "Gare Dorion/Chicoine/St-Antoine",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "9",
                shortName = "9",
                longName = "Gare Vaudreuil/Gare Dorion/Marier",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "91",
                shortName = "91",
                longName = "Gérald-Godin / Pointe-Claire",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "T1",
                shortName = "T1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "T2",
                shortName = "T2",
                longName = "Gare Vaudreuil/Bourget/des Sarcelles",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "T3",
                shortName = "T3",
                longName = "Gare Dorion/Valois/Tonnacour/Chicoine/St-Antoine",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "T31",
                shortName = "T31",
                longName = "Gare Pincourt T-V/Cardinal-Léger",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "T46",
                shortName = "T46",
                longName = "Gare Île-Perrot/Virginie-Roy/Pointe-du-Domaine",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "T5",
                shortName = "T5",
                longName = "Gare Vaudreuil/Gare Dorion",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "T6",
                shortName = "T6",
                longName = "Gare Vaudreuil/Émile-Bouchard/Ouimet",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
        assertThat(routes).contains(
            Route(
                _id = "T9",
                shortName = "T9",
                longName = "Gare Vaudreuil/Gare Dorion/Marier",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
    }

    private fun importedAllStops(version: String) {
        val stops = stopRepository.findAll()
        assertThat(stops).contains(Stop(_id = "71998", name = "1e Boulevard / 11e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "71999", name = "1e Boulevard / 11e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72000", name = "Oakland / Lakeview", version = version))
        assertThat(stops).contains(Stop(_id = "72001", name = "Lakeview / Maple", version = version))
        assertThat(stops).contains(Stop(_id = "72002", name = "Lakeview / Selkirk", version = version))
        assertThat(stops).contains(Stop(_id = "72003", name = "Lakeview / Cedar", version = version))
        assertThat(stops).contains(Stop(_id = "72004", name = "Lakeview / Cameron", version = version))
        assertThat(stops).contains(Stop(_id = "72005", name = "Cameron / Saint-Jean", version = version))
        assertThat(stops).contains(Stop(_id = "72006", name = "Cameron / Main", version = version))
        assertThat(stops).contains(Stop(_id = "72007", name = "Côte Saint-Charles / Bridle Path", version = version))
        assertThat(stops).contains(Stop(_id = "72008", name = "Main / Mount Pleasant", version = version))
        assertThat(stops).contains(Stop(_id = "72009", name = "Mount Pleasant / devant le #90", version = version))
        assertThat(stops).contains(Stop(_id = "72010", name = "Mount Pleasant / Upper McNaughten", version = version))
        assertThat(stops).contains(Stop(_id = "72011", name = "Mount Pleasant / Cameron", version = version))
        assertThat(stops).contains(Stop(_id = "72012", name = "Cameron / Wellesley", version = version))
        assertThat(stops).contains(Stop(_id = "72013", name = "Cameron / Fairhaven", version = version))
        assertThat(stops).contains(Stop(_id = "72014", name = "Côte Saint-Charles / Windcrest", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72015",
                name = "Côte Saint-Charles / devant l'École Westwood",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72016", name = "Côte Saint-Charles / Forestview", version = version))
        assertThat(stops).contains(Stop(_id = "72017", name = "Côte Saint-Charles / Hillside", version = version))
        assertThat(stops).contains(Stop(_id = "72018", name = "Côte Saint-Charles / Ridge", version = version))
        assertThat(stops).contains(Stop(_id = "72019", name = "Côte Saint-Charles / Main", version = version))
        assertThat(stops).contains(Stop(_id = "72020", name = "de Provence / Raimbeau", version = version))
        assertThat(stops).contains(Stop(_id = "72021", name = "de Provence / Raimbeau", version = version))
        assertThat(stops).contains(Stop(_id = "72022", name = "Montée Sagala / Henriette", version = version))
        assertThat(stops).contains(Stop(_id = "72023", name = "Montée Sagala / Henriette", version = version))
        assertThat(stops).contains(Stop(_id = "72024", name = "Grand Boulevard / 4e Rue", version = version))
        assertThat(stops).contains(Stop(_id = "72025", name = "4e Rue / Grand Boulevard", version = version))
        assertThat(stops).contains(Stop(_id = "72026", name = "Grand Boulevard / 4e Rue", version = version))
        assertThat(stops).contains(Stop(_id = "72027", name = "4e Rue / 7e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72028", name = "4e Rue / 7e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72029", name = "Boischatel / de Provence", version = version))
        assertThat(stops).contains(Stop(_id = "72030", name = "Boischatel / de Provence", version = version))
        assertThat(stops).contains(Stop(_id = "72031", name = "du Boisé / 4e Rue", version = version))
        assertThat(stops).contains(Stop(_id = "72032", name = "4e Rue / du Boisé", version = version))
        assertThat(stops).contains(Stop(_id = "72033", name = "Boischatel / des Bouleaux", version = version))
        assertThat(stops).contains(Stop(_id = "72034", name = "Boischatel / des Bouleaux", version = version))
        assertThat(stops).contains(Stop(_id = "72035", name = "Don-Quichotte / 24e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72036", name = "Don-Quichotte / devant le LILO", version = version))
        assertThat(stops).contains(Stop(_id = "72037", name = "Grand Boulevard / Don-Quichotte", version = version))
        assertThat(stops).contains(Stop(_id = "72038", name = "Don-Quichotte / Grand Boulevard", version = version))
        assertThat(stops).contains(Stop(_id = "72039", name = "Don-Quichotte / Grand Boulevard", version = version))
        assertThat(stops).contains(Stop(_id = "72040", name = "Gare Île-Perrot Quai 4", version = version))
        assertThat(stops).contains(Stop(_id = "72041", name = "Gare Île-Perrot Quai 3", version = version))
        assertThat(stops).contains(Stop(_id = "72042", name = "Grand Boulevard / devant le #405", version = version))
        assertThat(stops).contains(Stop(_id = "72043", name = "Grand Boulevard / devant le #400", version = version))
        assertThat(stops).contains(Stop(_id = "72044", name = "de Provence / de Marseille", version = version))
        assertThat(stops).contains(Stop(_id = "72045", name = "de Provence / de Marseille", version = version))
        assertThat(stops).contains(Stop(_id = "72046", name = "des Érables / Giffard", version = version))
        assertThat(stops).contains(Stop(_id = "72047", name = "des Érables / Giffard", version = version))
        assertThat(stops).contains(Stop(_id = "72048", name = "des Érables / du Port-Joli", version = version))
        assertThat(stops).contains(Stop(_id = "72049", name = "des Érables / du Port-Joli", version = version))
        assertThat(stops).contains(Stop(_id = "72050", name = "des Érables / des Hêtres", version = version))
        assertThat(stops).contains(Stop(_id = "72051", name = "des Érables / des Hêtres", version = version))
        assertThat(stops).contains(Stop(_id = "72052", name = "des Érables / 25e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72053", name = "des Érables / 25e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72054", name = "23e Avenue / des Érables", version = version))
        assertThat(stops).contains(Stop(_id = "72055", name = "des Érables / 23e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72056", name = "23e Avenue / 4e Rue", version = version))
        assertThat(stops).contains(Stop(_id = "72057", name = "4e Rue / 23e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72058", name = "4e Rue / des Ancolies", version = version))
        assertThat(stops).contains(Stop(_id = "72059", name = "4e Rue / des Ancolies", version = version))
        assertThat(stops).contains(Stop(_id = "72060", name = "du Parc / des Fougères", version = version))
        assertThat(stops).contains(Stop(_id = "72061", name = "Perrot / 10e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72062", name = "10e Avenue / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72063", name = "Perrot / 10e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72064", name = "Perrot / 7e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72065", name = "Perrot / 7e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72066", name = "Grand Boulevard / Saint-Pierre", version = version))
        assertThat(stops).contains(Stop(_id = "72067", name = "Grand Boulevard / Saint-Pierre", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72068",
                name = "Don-Quichotte / devant Carrefour Don-Quichotte",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72069",
                name = "Don-Quichotte / face au Carrefour Don-Quichotte",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72070", name = "Grand Boulevard / 4e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72071", name = "Grand Boulevard / 4e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72072", name = "5e Avenue / Grand Boulevard", version = version))
        assertThat(stops).contains(Stop(_id = "72073", name = "5e Avenue / Grand Boulevard", version = version))
        assertThat(stops).contains(Stop(_id = "72074", name = "Aurélien-Séguin / 8e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72075", name = "8e Avenue / Aurélien-Séguin", version = version))
        assertThat(stops).contains(Stop(_id = "72076", name = "10e Avenue / Aurélien-Séguin", version = version))
        assertThat(stops).contains(Stop(_id = "72077", name = "Aurélien-Séguin / 10e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72078", name = "Perrot / 23e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72079", name = "Perrot / 23e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72080", name = "27e Avenue / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72081", name = "Perrot / 27e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72082", name = "Perrot / 27e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72083", name = "des Ruisseaux / 27e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72084", name = "27e Avenue / des Ruisseaux", version = version))
        assertThat(stops).contains(Stop(_id = "72085", name = "des Ruisseaux / face au 145", version = version))
        assertThat(stops).contains(Stop(_id = "72086", name = "des Ruisseaux / face au 150", version = version))
        assertThat(stops).contains(Stop(_id = "72087", name = "Datura / des Ruisseaux", version = version))
        assertThat(stops).contains(Stop(_id = "72088", name = "des Ruisseaux / Datura", version = version))
        assertThat(stops).contains(Stop(_id = "72089", name = "Datura / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72090", name = "Perrot / Datura", version = version))
        assertThat(stops).contains(Stop(_id = "72091", name = "Perrot / Datura", version = version))
        assertThat(stops).contains(Stop(_id = "72092", name = "Perrot / 21e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72093", name = "Perrot / 21e Avenue", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72094",
                name = "Don-Quichotte / devant Galeries Don-Quichotte",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72095", name = "Don-Quichotte / des Rosiers", version = version))
        assertThat(stops).contains(Stop(_id = "72096", name = "Don-Quichotte / des Rosiers", version = version))
        assertThat(stops).contains(Stop(_id = "72097", name = "Grand Boulevard / René-Émard", version = version))
        assertThat(stops).contains(Stop(_id = "72098", name = "René-Émard / Grand Boulevard", version = version))
        assertThat(stops).contains(Stop(_id = "72099", name = "René-Émard / Lucien-Manning", version = version))
        assertThat(stops).contains(Stop(_id = "72100", name = "Lucien-Manning / René-Émard", version = version))
        assertThat(stops).contains(Stop(_id = "72101", name = "Grand Boulevard / Lucien-Manning", version = version))
        assertThat(stops).contains(Stop(_id = "72102", name = "Lucien-Manning / Grand Boulevard", version = version))
        assertThat(stops).contains(Stop(_id = "72103", name = "Grand Boulevard / Lucien-Manning", version = version))
        assertThat(stops).contains(Stop(_id = "72104", name = "Grand Boulevard / Lucien-Manning", version = version))
        assertThat(stops).contains(Stop(_id = "72105", name = "Lucien-Manning / Grand Boulevard", version = version))
        assertThat(stops).contains(Stop(_id = "72106", name = "25e Avenue / Boischatel", version = version))
        assertThat(stops).contains(Stop(_id = "72107", name = "Boischatel / 25e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72108", name = "25e Avenue / des Ormes", version = version))
        assertThat(stops).contains(Stop(_id = "72109", name = "25e Avenue / des Ormes", version = version))
        assertThat(stops).contains(Stop(_id = "72110", name = "des Lilas / des Peupliers", version = version))
        assertThat(stops).contains(Stop(_id = "72111", name = "des Lilas / des Peupliers", version = version))
        assertThat(stops).contains(Stop(_id = "72112", name = "des Rosiers / des Lilas", version = version))
        assertThat(stops).contains(Stop(_id = "72113", name = "des Rosiers / des Lilas", version = version))
        assertThat(stops).contains(Stop(_id = "72114", name = "du Boisé / du Parc", version = version))
        assertThat(stops).contains(Stop(_id = "72115", name = "Don-Quichotte / de Provence", version = version))
        assertThat(stops).contains(Stop(_id = "72116", name = "Don-Quichotte / de Provence", version = version))
        assertThat(stops).contains(Stop(_id = "72117", name = "8e Avenue / 7e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72118", name = "7e Avenue / 8e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72119", name = "5e Avenue / Montée Sagala", version = version))
        assertThat(stops).contains(Stop(_id = "72120", name = "Montée Sagala / 5e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72121", name = "Grand Boulevard / 1re Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72122", name = "1re Avenue / Grand Boulevard", version = version))
        assertThat(stops).contains(Stop(_id = "72123", name = "5e Avenue / devant le #97", version = version))
        assertThat(stops).contains(Stop(_id = "72124", name = "5e Avenue / devant le #96", version = version))
        assertThat(stops).contains(Stop(_id = "72125", name = "du Domaine / Trenet", version = version))
        assertThat(stops).contains(Stop(_id = "72126", name = "Trenet / du Domaine", version = version))
        assertThat(stops).contains(Stop(_id = "72127", name = "Alexis-Trottier / du Domaine", version = version))
        assertThat(stops).contains(Stop(_id = "72128", name = "Alexis-Trottier / Étienne-Trudeau", version = version))
        assertThat(stops).contains(Stop(_id = "72129", name = "Perrot / 63e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72130", name = "Perrot / 63e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72131", name = "Perrot / face au #1659", version = version))
        assertThat(stops).contains(Stop(_id = "72132", name = "Perrot / devant le #1659", version = version))
        assertThat(stops).contains(Stop(_id = "72133", name = "Caza / Promenade Saint-Louis", version = version))
        assertThat(stops).contains(Stop(_id = "72134", name = "Promenade Saint-Louis / Caza", version = version))
        assertThat(stops).contains(Stop(_id = "72135", name = "Caza / Carrière", version = version))
        assertThat(stops).contains(Stop(_id = "72136", name = "Caza / Carrière", version = version))
        assertThat(stops).contains(Stop(_id = "72137", name = "Caza / Aumais", version = version))
        assertThat(stops).contains(Stop(_id = "72138", name = "Aumais / Caza", version = version))
        assertThat(stops).contains(Stop(_id = "72139", name = "Caza / Émile-Nelligan", version = version))
        assertThat(stops).contains(Stop(_id = "72140", name = "Caza / Émile-Nelligan", version = version))
        assertThat(stops).contains(Stop(_id = "72141", name = "Caza / Daoust", version = version))
        assertThat(stops).contains(Stop(_id = "72142", name = "Caza / Daoust", version = version))
        assertThat(stops).contains(Stop(_id = "72143", name = "Caza / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72144", name = "Caza / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72145", name = "Perrot / Alfred-Pellan", version = version))
        assertThat(stops).contains(Stop(_id = "72146", name = "Perrot / Alfred-Pellan", version = version))
        assertThat(stops).contains(Stop(_id = "72147", name = "Perrot / face au Parc des Fauvettes", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72148",
                name = "Perrot / devant le Parc des Fauvettes",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72149", name = "Perrot / devant le #2187", version = version))
        assertThat(stops).contains(Stop(_id = "72150", name = "Perrot / face au #2180", version = version))
        assertThat(stops).contains(Stop(_id = "72151", name = "Perrot / Marie-Marthe-Daoust", version = version))
        assertThat(stops).contains(Stop(_id = "72152", name = "Perrot / Marie-Marthe-Daoust", version = version))
        assertThat(stops).contains(Stop(_id = "72153", name = "Don-Quichotte / Perrier", version = version))
        assertThat(stops).contains(Stop(_id = "72154", name = "Don-Quichotte / Perrier", version = version))
        assertThat(stops).contains(Stop(_id = "72155", name = "Perrot / face au #1473", version = version))
        assertThat(stops).contains(Stop(_id = "72156", name = "Perrot / devant le #1473", version = version))
        assertThat(stops).contains(Stop(_id = "72157", name = "Forest / du Pinacle", version = version))
        assertThat(stops).contains(Stop(_id = "72158", name = "Forest / du Pinacle", version = version))
        assertThat(stops).contains(Stop(_id = "72159", name = "Perrier / Pascal", version = version))
        assertThat(stops).contains(Stop(_id = "72160", name = "Perrier / Pascal", version = version))
        assertThat(stops).contains(Stop(_id = "72161", name = "Forest / Huot", version = version))
        assertThat(stops).contains(Stop(_id = "72162", name = "Forest / Huot", version = version))
        assertThat(stops).contains(Stop(_id = "72163", name = "de la Rivelaine / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72164", name = "Perrot / de la Rivelaine", version = version))
        assertThat(stops).contains(Stop(_id = "72165", name = "Perrot / de la Rivelaine", version = version))
        assertThat(stops).contains(Stop(_id = "72166", name = "de la Rivelaine / des Roseaux", version = version))
        assertThat(stops).contains(Stop(_id = "72167", name = "de la Rivelaine / des Roseaux", version = version))
        assertThat(stops).contains(Stop(_id = "72168", name = "de la Rivelaine / Lucille-Teasdale", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72169",
                name = "de la Rivelaine / de la Reine-des-Prés",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72170", name = "de la Rivelaine / de la Rhapsodie", version = version))
        assertThat(stops).contains(Stop(_id = "72171", name = "de la Rivelaine / de la Rhapsodie", version = version))
        assertThat(stops).contains(Stop(_id = "72172", name = "Alexis-Trottier / de la Passe", version = version))
        assertThat(stops).contains(Stop(_id = "72173", name = "Don-Quichotte / Alfred-Laliberté", version = version))
        assertThat(stops).contains(Stop(_id = "72174", name = "Don-Quichotte / Alfred-Laliberté", version = version))
        assertThat(stops).contains(Stop(_id = "72175", name = "Jordi-Bonet / Don-Quichotte", version = version))
        assertThat(stops).contains(Stop(_id = "72176", name = "Don-Quichotte / Jordi-Bonet", version = version))
        assertThat(stops).contains(Stop(_id = "72177", name = "Jordi-Bonet / Pauline-Julien", version = version))
        assertThat(stops).contains(Stop(_id = "72178", name = "Jordi-Bonet / Pauline-Julien", version = version))
        assertThat(stops).contains(Stop(_id = "72179", name = "Jordi-Bonet / Promenade Saint-Louis", version = version))
        assertThat(stops).contains(Stop(_id = "72180", name = "Promenade Saint-Louis / Jordi-Bonet", version = version))
        assertThat(stops).contains(Stop(_id = "72181", name = "Don-Quichotte / de la Rivelaine", version = version))
        assertThat(stops).contains(Stop(_id = "72182", name = "Don-Quichotte / de la Rivelaine", version = version))
        assertThat(stops).contains(Stop(_id = "72183", name = "Promenade Saint-Louis / Kay", version = version))
        assertThat(stops).contains(Stop(_id = "72184", name = "Promenade Saint-Louis / Kay", version = version))
        assertThat(stops).contains(Stop(_id = "72185", name = "Aumais / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72186", name = "Aumais / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72187", name = "Don-Quichotte / Rouleau", version = version))
        assertThat(stops).contains(Stop(_id = "72188", name = "Don-Quichotte / Rouleau", version = version))
        assertThat(stops).contains(Stop(_id = "72189", name = "Don-Quichotte / 22e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72190", name = "Don-Quichotte / 22e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72191", name = "Perrot / de l'Église", version = version))
        assertThat(stops).contains(Stop(_id = "72192", name = "Perrot / de l'Église", version = version))
        assertThat(stops).contains(Stop(_id = "72193", name = "Saint-Joseph / Virginie-Roy", version = version))
        assertThat(stops).contains(Stop(_id = "72194", name = "Saint-Joseph / Virginie-Roy", version = version))
        assertThat(stops).contains(Stop(_id = "72195", name = "Perrier / Picasso", version = version))
        assertThat(stops).contains(Stop(_id = "72196", name = "Perrier / Picasso", version = version))
        assertThat(stops).contains(Stop(_id = "72197", name = "Perrier / Pothier", version = version))
        assertThat(stops).contains(Stop(_id = "72198", name = "Perrier / Pothier", version = version))
        assertThat(stops).contains(Stop(_id = "72199", name = "Perrier / Michel-McNabb", version = version))
        assertThat(stops).contains(Stop(_id = "72200", name = "Michel-McNabb / Perrier", version = version))
        assertThat(stops).contains(Stop(_id = "72201", name = "Michel-McNabb / Jean-Paul-Lemieux", version = version))
        assertThat(stops).contains(Stop(_id = "72202", name = "Michel-McNabb / Jean-Paul-Lemieux", version = version))
        assertThat(stops).contains(Stop(_id = "72203", name = "Estelle-Mauffette / Léo-Ayotte", version = version))
        assertThat(stops).contains(Stop(_id = "72204", name = "Estelle-Mauffette / Léo-Ayotte", version = version))
        assertThat(stops).contains(Stop(_id = "72205", name = "de la Rivelaine / Francois-Rapin", version = version))
        assertThat(stops).contains(Stop(_id = "72206", name = "Francois-Rapin / de la Rivelaine", version = version))
        assertThat(stops).contains(Stop(_id = "72207", name = "de la Rivelaine / Marie-Rollet", version = version))
        assertThat(stops).contains(Stop(_id = "72208", name = "de la Rivelaine / Marie-Rollet", version = version))
        assertThat(stops).contains(Stop(_id = "72209", name = "Perrot / 144e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72210", name = "Perrot / 144e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72211", name = "de la Rivelaine / Virginie-Roy", version = version))
        assertThat(stops).contains(Stop(_id = "72212", name = "Virginie-Roy / de la Rivelaine", version = version))
        assertThat(stops).contains(Stop(_id = "72213", name = "Virginie-Roy / Rémillard", version = version))
        assertThat(stops).contains(Stop(_id = "72214", name = "Virginie-Roy / Rémillard", version = version))
        assertThat(stops).contains(Stop(_id = "72215", name = "Virginie-Roy / Rhéaume", version = version))
        assertThat(stops).contains(Stop(_id = "72216", name = "Virginie-Roy / Rhéaume", version = version))
        assertThat(stops).contains(Stop(_id = "72217", name = "Don-Quichotte / Forest", version = version))
        assertThat(stops).contains(Stop(_id = "72218", name = "Perrot / Robillard", version = version))
        assertThat(stops).contains(Stop(_id = "72219", name = "Perrot / 40e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72220", name = "Perrot / Renaud", version = version))
        assertThat(stops).contains(Stop(_id = "72221", name = "Perrot / Jean-Paul-Lemieux", version = version))
        assertThat(stops).contains(Stop(_id = "72222", name = "Perrot / Jean-Paul-Lemieux", version = version))
        assertThat(stops).contains(Stop(_id = "72223", name = "Perrot / Pasteur", version = version))
        assertThat(stops).contains(Stop(_id = "72224", name = "Perrot / Pasteur", version = version))
        assertThat(stops).contains(Stop(_id = "72225", name = "Perrot / 53e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72226", name = "Perrot / 53e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72227", name = "Perrot / 60e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72228", name = "Perrot / 60e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72229", name = "Perrot / 56e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72230", name = "Perrot / 56e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72231", name = "Perrot / devant le #1334", version = version))
        assertThat(stops).contains(Stop(_id = "72232", name = "Perrot / devant le #1331", version = version))
        assertThat(stops).contains(Stop(_id = "72233", name = "du Domaine / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72234", name = "du Domaine / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72235", name = "du Domaine / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72236", name = "du Domaine / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72237", name = "Perrot / 146e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72238", name = "Perrot / 146e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72239", name = "Perrot / 100e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72240", name = "Perrot / 100e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72241", name = "Perrot / 87e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72242", name = "Perrot / 87e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72243", name = "Perrot / 81e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72244", name = "Perrot / 81e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72245", name = "Don-Quichotte / Iberville", version = version))
        assertThat(stops).contains(Stop(_id = "72246", name = "Don-Quichotte / Iberville", version = version))
        assertThat(stops).contains(Stop(_id = "72247", name = "Perrot / 150e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72248", name = "Perrot / 150e Avenue", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72249",
                name = "Antoine-De La Fresnaye / Virginie-Roy",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72250",
                name = "Antoine-De La Fresnaye / Virginie-Roy",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72251",
                name = "Francoise-Cuillerier / Charles-Le Moyne",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72252",
                name = "Francoise-Cuillerier / Charles-Le Moyne",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72253", name = "Francoise-Cuillerier / Hubert-Leduc", version = version))
        assertThat(stops).contains(Stop(_id = "72254", name = "Hubert-Leduc / Francoise-Cuillerier", version = version))
        assertThat(stops).contains(Stop(_id = "72255", name = "Virginie-Roy / Jean-Paul-Lemieux", version = version))
        assertThat(stops).contains(Stop(_id = "72256", name = "Virginie-Roy / Jean-Paul-Lemieux", version = version))
        assertThat(stops).contains(Stop(_id = "72257", name = "Rouleau / Rester", version = version))
        assertThat(stops).contains(Stop(_id = "72258", name = "Rouleau / Rester", version = version))
        assertThat(stops).contains(Stop(_id = "72259", name = "Rouleau / Francois-Rapin", version = version))
        assertThat(stops).contains(Stop(_id = "72260", name = "Francois-Rapin / Rouleau", version = version))
        assertThat(stops).contains(Stop(_id = "72261", name = "Roger-Maillet / Lucien-Thériault", version = version))
        assertThat(stops).contains(Stop(_id = "72262", name = "Roger-Maillet / Lucien-Thériault", version = version))
        assertThat(stops).contains(Stop(_id = "72263", name = "Saint-Joseph / Perrot", version = version))
        assertThat(stops).contains(Stop(_id = "72264", name = "Perrot / Saint-Joseph", version = version))
        assertThat(stops).contains(Stop(_id = "72265", name = "Lucien-Thériault / Sylvio-Leduc", version = version))
        assertThat(stops).contains(Stop(_id = "72266", name = "Sylvio-Leduc / Lucien-Thériault", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72267",
                name = "Antoine-De La Fresnaye / Lucien-Thériault",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72268",
                name = "Lucien-Thériault / Antoine-De La Fresnaye",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72269", name = "Pasteur / Virginie-Roy", version = version))
        assertThat(stops).contains(Stop(_id = "72270", name = "Pasteur / Virginie-Roy", version = version))
        assertThat(stops).contains(Stop(_id = "72271", name = "Pasteur / Parmentier", version = version))
        assertThat(stops).contains(Stop(_id = "72272", name = "Pasteur / Parmentier", version = version))
        assertThat(stops).contains(Stop(_id = "72273", name = "Pasteur / Ponsard", version = version))
        assertThat(stops).contains(Stop(_id = "72274", name = "Pasteur / Ponsard", version = version))
        assertThat(stops).contains(Stop(_id = "72275", name = "Pasteur / Paré", version = version))
        assertThat(stops).contains(Stop(_id = "72276", name = "Pasteur / Paré", version = version))
        assertThat(stops).contains(Stop(_id = "72277", name = "Perrot / 38e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72278", name = "Perrot / Saint-Joseph", version = version))
        assertThat(stops).contains(Stop(_id = "72279", name = "Saint-Joseph / Perrot", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72280",
                name = "Auto-Plaza / Brunswick (centre Fairview)",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72281",
                name = "Auto-Plaza / Brunswick (centre Fairview)",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72282", name = "23e Avenue / Olympique", version = version))
        assertThat(stops).contains(Stop(_id = "72283", name = "Olympique / 23e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72284", name = "de l'Île / du Suroît", version = version))
        assertThat(stops).contains(Stop(_id = "72285", name = "de l'Île / du Suroît", version = version))
        assertThat(stops).contains(Stop(_id = "72286", name = "Olympique / Huneault", version = version))
        assertThat(stops).contains(Stop(_id = "72287", name = "Olympique / Huneault", version = version))
        assertThat(stops).contains(Stop(_id = "72288", name = "Pincourt / 5e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72289", name = "Pincourt / 5e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72290", name = "Pincourt / 5e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72291", name = "Pincourt / du Versant", version = version))
        assertThat(stops).contains(Stop(_id = "72292", name = "Pincourt / de la Plaine", version = version))
        assertThat(stops).contains(Stop(_id = "72293", name = "Forest / Noyer", version = version))
        assertThat(stops).contains(Stop(_id = "72294", name = "Forest / Noyer", version = version))
        assertThat(stops).contains(Stop(_id = "72295", name = "Frontenac / Montcalm", version = version))
        assertThat(stops).contains(Stop(_id = "72296", name = "Montcalm / Frontenac", version = version))
        assertThat(stops).contains(Stop(_id = "72297", name = "des Orioles / Renaissance", version = version))
        assertThat(stops).contains(Stop(_id = "72298", name = "des Orioles / Renaissance", version = version))
        assertThat(stops).contains(Stop(_id = "72299", name = "des Roitelets / des Orioles", version = version))
        assertThat(stops).contains(Stop(_id = "72300", name = "des Orioles / des Roitelets", version = version))
        assertThat(stops).contains(Stop(_id = "72301", name = "Renaissance / des Roitelets", version = version))
        assertThat(stops).contains(Stop(_id = "72302", name = "des Roitelets / Renaissance", version = version))
        assertThat(stops).contains(Stop(_id = "72303", name = "Renaissance / des Bruants", version = version))
        assertThat(stops).contains(Stop(_id = "72304", name = "Renaissance / des Bruants", version = version))
        assertThat(stops).contains(Stop(_id = "72305", name = "Renaissance / Radisson", version = version))
        assertThat(stops).contains(Stop(_id = "72306", name = "Kendall / Radisson", version = version))
        assertThat(stops).contains(Stop(_id = "72307", name = "Olympique / du Sous-Bois", version = version))
        assertThat(stops).contains(Stop(_id = "72308", name = "Olympique / du Sous-Bois", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72309",
                name = "Cardinal-Léger /devant le #107 (Metro)",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72310",
                name = "Cardinal-Léger / devant le Maxi & Cie",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72311", name = "Olympique / Lussier", version = version))
        assertThat(stops).contains(Stop(_id = "72312", name = "Olympique / Lussier", version = version))
        assertThat(stops).contains(Stop(_id = "72313", name = "Lussier / Bellevue", version = version))
        assertThat(stops).contains(Stop(_id = "72314", name = "Lussier / Bellevue", version = version))
        assertThat(stops).contains(Stop(_id = "72315", name = "Shamrock / Northcote", version = version))
        assertThat(stops).contains(Stop(_id = "72316", name = "Northcote / Shamrock", version = version))
        assertThat(stops).contains(Stop(_id = "72317", name = "Cardinal-Léger / 7e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72318", name = "Cardinal-Léger / de l'Île", version = version))
        assertThat(stops).contains(Stop(_id = "72319", name = "de l'Île / Cardinal-Léger", version = version))
        assertThat(stops).contains(Stop(_id = "72320", name = "Cardinal-Léger / de l'Île", version = version))
        assertThat(stops).contains(Stop(_id = "72321", name = "de l'Île / des Merisiers", version = version))
        assertThat(stops).contains(Stop(_id = "72322", name = "de l'Île / des Merisiers", version = version))
        assertThat(stops).contains(Stop(_id = "72323", name = "des Merisiers / de l'Île", version = version))
        assertThat(stops).contains(Stop(_id = "72324", name = "Cardinal-Léger / Lussier", version = version))
        assertThat(stops).contains(Stop(_id = "72325", name = "Lussier / Cardinal-Léger", version = version))
        assertThat(stops).contains(Stop(_id = "72326", name = "Forest / de la Plaine", version = version))
        assertThat(stops).contains(Stop(_id = "72327", name = "Forest / de la Vallée", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72328",
                name = "du Traversier / face au #1900 (Patrick Morin)",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72329",
                name = "du Traversier / devant le  #1900 (Patrick Morin)",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72330", name = "des Buissons / des Merisiers", version = version))
        assertThat(stops).contains(Stop(_id = "72331", name = "des Merisiers / des Buissons", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72332",
                name = "Faubourg de l'Île / devant l'entrée #4",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72333", name = "Cardinal-Léger / Forest", version = version))
        assertThat(stops).contains(Stop(_id = "72334", name = "Cardinal-Léger / Forest", version = version))
        assertThat(stops).contains(Stop(_id = "72335", name = "Joseph-Laflèche / devant le #49", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72336",
                name = "Cardinal-Léger / Monseigneur-Langlois",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72337",
                name = "Cardinal-Léger / Monseigneur-Langlois",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72338", name = "Régent / Forest", version = version))
        assertThat(stops).contains(Stop(_id = "72339", name = "Forest / Régent", version = version))
        assertThat(stops).contains(Stop(_id = "72340", name = "Cardinal-Léger / 8e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72341", name = "Cardinal-Léger / 5e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72342", name = "Cardinal-Léger / 5e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72343", name = "Gare Pincourt / Terrasse-Vaudreuil", version = version))
        assertThat(stops).contains(Stop(_id = "72344", name = "du Traversier / Cardinal-Léger", version = version))
        assertThat(stops).contains(Stop(_id = "72345", name = "du Traversier / Cardinal-Léger", version = version))
        assertThat(stops).contains(Stop(_id = "72346", name = "Régent / Marcotte", version = version))
        assertThat(stops).contains(Stop(_id = "72347", name = "Régent / Marcotte", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72348",
                name = "Cardinal-Léger / devant le Parc Trotter",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72349",
                name = "Cardinal-Léger / face au Parc Trotter",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72350",
                name = "Cardinal-Léger / devant le #106 (Tabagie Pincourt)",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72351",
                name = "Cardinal-Léger / devant le Pharmaprix",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72352", name = "Cardinal-Léger / des Frênes", version = version))
        assertThat(stops).contains(Stop(_id = "72354", name = "44e Avenue / Duhamel", version = version))
        assertThat(stops).contains(Stop(_id = "72355", name = "44e Avenue / Duhamel", version = version))
        assertThat(stops).contains(Stop(_id = "72356", name = "44e Avenue / 3e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72357", name = "3e Avenue / 44e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72358", name = "3e Avenue / Cardinal-Léger", version = version))
        assertThat(stops).contains(Stop(_id = "72359", name = "Cardinal-Léger / 3e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72362", name = "de l'Île / Forest", version = version))
        assertThat(stops).contains(Stop(_id = "72363", name = "Forest / de l'Île", version = version))
        assertThat(stops).contains(Stop(_id = "72364", name = "de l'Île / Forest", version = version))
        assertThat(stops).contains(Stop(_id = "72365", name = "Forest / de l'Île", version = version))
        assertThat(stops).contains(Stop(_id = "72366", name = "Forest / Shamrock", version = version))
        assertThat(stops).contains(Stop(_id = "72367", name = "Shamrock / Forest", version = version))
        assertThat(stops).contains(Stop(_id = "72368", name = "Shamrock / Bayview", version = version))
        assertThat(stops).contains(Stop(_id = "72369", name = "Shamrock / Bayview", version = version))
        assertThat(stops).contains(Stop(_id = "72370", name = "de l'Île / du Suroît", version = version))
        assertThat(stops).contains(Stop(_id = "72371", name = "du Suroît / de l'Île", version = version))
        assertThat(stops).contains(Stop(_id = "72372", name = "de l'Île / du Suroît", version = version))
        assertThat(stops).contains(Stop(_id = "72373", name = "de l'Île / du Suroît", version = version))
        assertThat(stops).contains(Stop(_id = "72374", name = "Forest / Pincourt", version = version))
        assertThat(stops).contains(Stop(_id = "72375", name = "Pincourt / Forest", version = version))
        assertThat(stops).contains(Stop(_id = "72376", name = "Forest / Pincourt", version = version))
        assertThat(stops).contains(Stop(_id = "72377", name = "Forest / Racine", version = version))
        assertThat(stops).contains(Stop(_id = "72378", name = "Forest / Racine", version = version))
        assertThat(stops).contains(Stop(_id = "72379", name = "Forest / devant le #550", version = version))
        assertThat(stops).contains(Stop(_id = "72380", name = "Forest / de la Cime", version = version))
        assertThat(stops).contains(Stop(_id = "72381", name = "Forest / de la Falaise", version = version))
        assertThat(stops).contains(Stop(_id = "72382", name = "Forest / de la Falaise", version = version))
        assertThat(stops).contains(Stop(_id = "72383", name = "de l'Île / Northcote", version = version))
        assertThat(stops).contains(Stop(_id = "72384", name = "de l'Île / Northcote", version = version))
        assertThat(stops).contains(Stop(_id = "72385", name = "Northcote / de l'Île", version = version))
        assertThat(stops).contains(Stop(_id = "72386", name = "de l'Île / Montcalm", version = version))
        assertThat(stops).contains(Stop(_id = "72387", name = "Montcalm / de l'Île", version = version))
        assertThat(stops).contains(Stop(_id = "72388", name = "Montfort / Dumas", version = version))
        assertThat(stops).contains(Stop(_id = "72389", name = "Dumas / Montfort", version = version))
        assertThat(stops).contains(Stop(_id = "72390", name = "Montfort / du Souvenir", version = version))
        assertThat(stops).contains(Stop(_id = "72391", name = "Montfort / du Souvenir", version = version))
        assertThat(stops).contains(Stop(_id = "72392", name = "Kendall / Montfort", version = version))
        assertThat(stops).contains(Stop(_id = "72393", name = "Montfort / Kendall", version = version))
        assertThat(stops).contains(Stop(_id = "72394", name = "de l'Île / 5e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72395", name = "de l'Île / 5e Avenue", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72396",
                name = "Cardinal-Léger / du Boisé-des-Chênes",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72397",
                name = "Cardinal-Léger / du Boisé-des-Chênes",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72398", name = "Olympique / 17e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72399", name = "Olympique / 17e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72400", name = "Northcote / Frontenac", version = version))
        assertThat(stops).contains(Stop(_id = "72401", name = "Frontenac / Northcote", version = version))
        assertThat(stops).contains(Stop(_id = "72402", name = "Northcote / Frontenac", version = version))
        assertThat(stops).contains(Stop(_id = "72403", name = "du Suroît / des Pommetiers", version = version))
        assertThat(stops).contains(Stop(_id = "72404", name = "du Suroît / des Pommetiers", version = version))
        assertThat(stops).contains(Stop(_id = "72405", name = "du Suroît / des Lauriers", version = version))
        assertThat(stops).contains(Stop(_id = "72406", name = "du Suroît / des Lauriers", version = version))
        assertThat(stops).contains(Stop(_id = "72407", name = "du Suroît / des Genévriers", version = version))
        assertThat(stops).contains(Stop(_id = "72408", name = "du Suroît / des Genévriers", version = version))
        assertThat(stops).contains(Stop(_id = "72409", name = "Cardinal-Léger / 23e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72410", name = "23e Avenue / Cardinal-Léger", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72411",
                name = "Montée Lavigne / devant le #101 (Esso)",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72412",
                name = "Montée Lavigne / devant le #98 (Petro Canada)",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72413",
                name = "Saint-Jean-Baptiste Est / devant le Métro",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72414",
                name = "Saint-Jean-Baptiste Est / face au Dairy Queen",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72415",
                name = "Saint-Viateur / du Boisé-des-Franciscaines",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72416",
                name = "Saint-Viateur / du Boisé-des-Franciscaines",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72419",
                name = "de la Coopérative / J.-Hyacinthe-Leduc",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72420",
                name = "de la Coopérative / J.-Hyacinthe-Leduc",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72421", name = "Saint-Antoine / de la Coopérative", version = version))
        assertThat(stops).contains(Stop(_id = "72422", name = "de la Coopérative / Saint-Antoine", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72423",
                name = "Saint-Viateur / Saint-Jean-Baptiste Est",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72424",
                name = "Saint-Viateur / Saint-Jean-Baptiste Est",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72425",
                name = "de l'Hôtel-de-Ville / Gérard-Chicoine",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72426",
                name = "de l'Hôtel-de-Ville / Gérard-Chicoine",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72427", name = "de l'Hôtel-de-Ville / Saint-Pierre", version = version))
        assertThat(stops).contains(Stop(_id = "72428", name = "de l'Hôtel-de-Ville / Saint-Pierre", version = version))
        assertThat(stops).contains(Stop(_id = "72429", name = "Saint-Pierre / de la Banque", version = version))
        assertThat(stops).contains(Stop(_id = "72430", name = "Saint-Pierre / de la Banque", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72431",
                name = "Saint-François / Saint-Jean-Baptiste Ouest",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72432",
                name = "Saint-François / Saint-Jean-Baptiste",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72433", name = "Joly / Saint-François", version = version))
        assertThat(stops).contains(Stop(_id = "72434", name = "Joly / Aimé-Aubry", version = version))
        assertThat(stops).contains(Stop(_id = "72435", name = "Bussière / Joly", version = version))
        assertThat(stops).contains(Stop(_id = "72436", name = "Bussière / Edgar-Séguin", version = version))
        assertThat(stops).contains(Stop(_id = "72437", name = "Bussière / Saint-François", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72438",
                name = "Saint-François / Simon-Pierre-Tremblay",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72439", name = "J.-Marc-Séguin / Saint-François", version = version))
        assertThat(stops).contains(Stop(_id = "72440", name = "J.-Marc-Séguin / Saint-François", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72441",
                name = "Séguin / devant le #26 (Fleury Michon)",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72442",
                name = "Séguin / face au #26 (Fleury Michon)",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72443",
                name = "J.-Marc-Séguin / Saint-Jean-Baptiste Ouest",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72444",
                name = "J.-Marc-Séguin / Saint-Jean-Baptiste Ouest",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72445",
                name = "de la Coopérative / Saint-Jean-Baptiste Est",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72446",
                name = "de la Coopérative / Saint-Jean-Baptiste Est",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72447", name = "Sainte-Anne / Lalonde", version = version))
        assertThat(stops).contains(Stop(_id = "72448", name = "Terminus Macdonald", version = version))
        assertThat(stops).contains(Stop(_id = "72449", name = "Terminus Macdonald", version = version))
        assertThat(stops).contains(Stop(_id = "72450", name = "Saint-Pierre / passage piétonnier", version = version))
        assertThat(stops).contains(Stop(_id = "72451", name = "du Collège / Saint-Pierre", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72452",
                name = "rue Poultry Cottages / face à la Ferme Macdonald",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72453", name = "John Abbott College", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72454",
                name = "rue Poultry Cottages / face à la Ferme Macdonald",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72455", name = "de la Côte-Vertu / Beaulac", version = version))
        assertThat(stops).contains(Stop(_id = "72456", name = "de la Côte-Vertu / Beaulac", version = version))
        assertThat(stops).contains(Stop(_id = "72457", name = "Terminus Côte-Vertu", version = version))
        assertThat(stops).contains(Stop(_id = "72458", name = "Terminus Côte-Vertu", version = version))
        assertThat(stops).contains(Stop(_id = "72459", name = "Yearling / Croissant Cavaletti", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72460",
                name = "côte St-Charles / face au #2760 (Mon Village)",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72461",
                name = "côte St-Charles / devant le #2760 (Mon Village)",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72462", name = "Master / Carriage Way", version = version))
        assertThat(stops).contains(Stop(_id = "72463", name = "Master / Carriage Way", version = version))
        assertThat(stops).contains(Stop(_id = "72464", name = "Yearling / Master", version = version))
        assertThat(stops).contains(Stop(_id = "72465", name = "Master / Yearling", version = version))
        assertThat(stops).contains(Stop(_id = "72466", name = "Yearling / de Bay Meadow", version = version))
        assertThat(stops).contains(Stop(_id = "72467", name = "Yearling / Pine Run", version = version))
        assertThat(stops).contains(Stop(_id = "72468", name = "Yearling / Pine Run", version = version))
        assertThat(stops).contains(Stop(_id = "72469", name = "Yearling / Stallion", version = version))
        assertThat(stops).contains(Stop(_id = "72470", name = "Yearling / Stallion", version = version))
        assertThat(stops).contains(Stop(_id = "72471", name = "Côte Saint-Charles / Harwood", version = version))
        assertThat(stops).contains(Stop(_id = "72472", name = "Côte Saint-Charles / Harwood", version = version))
        assertThat(stops).contains(Stop(_id = "72473", name = "Harwood / Côte Saint-Charles", version = version))
        assertThat(stops).contains(Stop(_id = "72474", name = "Yearling / du Bordelais", version = version))
        assertThat(stops).contains(Stop(_id = "72475", name = "Yearling / du Bordelais", version = version))
        assertThat(stops).contains(Stop(_id = "72476", name = "des Sables / Croissant du Sablier", version = version))
        assertThat(stops).contains(Stop(_id = "72477", name = "du Bordelais / du Conservatoire", version = version))
        assertThat(stops).contains(Stop(_id = "72478", name = "du Bordelais / du Conservatoire", version = version))
        assertThat(stops).contains(Stop(_id = "72479", name = "du Bordelais / Chanterel", version = version))
        assertThat(stops).contains(Stop(_id = "72480", name = "du Bordelais / Chanterel", version = version))
        assertThat(stops).contains(Stop(_id = "72481", name = "du Bordelais / du Bordeaux", version = version))
        assertThat(stops).contains(Stop(_id = "72482", name = "du Bordelais / du Bordeaux", version = version))
        assertThat(stops).contains(Stop(_id = "72483", name = "du Bordelais / Sainte-Élisabeth", version = version))
        assertThat(stops).contains(Stop(_id = "72484", name = "du Bordelais / Sainte-Élisabeth", version = version))
        assertThat(stops).contains(Stop(_id = "72485", name = "du Bordelais / des Oliviers", version = version))
        assertThat(stops).contains(Stop(_id = "72486", name = "du Bordelais / du Sauvignon", version = version))
        assertThat(stops).contains(Stop(_id = "72487", name = "du Bordelais / Place du Bordelais", version = version))
        assertThat(stops).contains(Stop(_id = "72488", name = "du Bordelais / du Muscadet", version = version))
        assertThat(stops).contains(Stop(_id = "72489", name = "Sainte-Angélique / du Bordelais", version = version))
        assertThat(stops).contains(Stop(_id = "72490", name = "du Bordelais / Sainte-Angélique", version = version))
        assertThat(stops).contains(Stop(_id = "72491", name = "Sainte-Angélique / des Cascades", version = version))
        assertThat(stops).contains(Stop(_id = "72492", name = "Sainte-Angélique / des Cascades", version = version))
        assertThat(stops).contains(Stop(_id = "72493", name = "Sainte-Angélique / Rozon", version = version))
        assertThat(stops).contains(Stop(_id = "72494", name = "Sainte-Angélique / Rozon", version = version))
        assertThat(stops).contains(Stop(_id = "72495", name = "Sainte-Angélique / Michel", version = version))
        assertThat(stops).contains(Stop(_id = "72496", name = "Sainte-Angélique / Michel", version = version))
        assertThat(stops).contains(Stop(_id = "72497", name = "Sainte-Angélique / Bédard", version = version))
        assertThat(stops).contains(Stop(_id = "72498", name = "Sainte-Angélique / Bédard", version = version))
        assertThat(stops).contains(Stop(_id = "72499", name = "Sainte-Angélique / des Marguerites", version = version))
        assertThat(stops).contains(Stop(_id = "72500", name = "Sainte-Angélique / des Marguerites", version = version))
        assertThat(stops).contains(Stop(_id = "72501", name = "Sainte-Angélique / Leroux", version = version))
        assertThat(stops).contains(Stop(_id = "72502", name = "Sainte-Angélique / Leroux", version = version))
        assertThat(stops).contains(Stop(_id = "72503", name = "Sainte-Angélique / Daniel", version = version))
        assertThat(stops).contains(Stop(_id = "72504", name = "Master / Croissant  Chestnut", version = version))
        assertThat(stops).contains(Stop(_id = "72505", name = "Master / Croissant  Chestnut", version = version))
        assertThat(stops).contains(Stop(_id = "72506", name = "Sainte-Angélique / des Cèdres", version = version))
        assertThat(stops).contains(Stop(_id = "72507", name = "Brazeau / Sainte-Angélique", version = version))
        assertThat(stops).contains(Stop(_id = "72508", name = "Brazeau / Sainte-Angélique", version = version))
        assertThat(stops).contains(Stop(_id = "72509", name = "du Printemps / Brazeau", version = version))
        assertThat(stops).contains(Stop(_id = "72510", name = "Brazeau / du Printemps", version = version))
        assertThat(stops).contains(Stop(_id = "72511", name = "des Semences / du Printemps", version = version))
        assertThat(stops).contains(Stop(_id = "72512", name = "du Printemps / des Semences", version = version))
        assertThat(stops).contains(Stop(_id = "72513", name = "des Semences / Champêtre", version = version))
        assertThat(stops).contains(Stop(_id = "72514", name = "Champêtre / des Semences", version = version))
        assertThat(stops).contains(Stop(_id = "72515", name = "Champêtre / du Sous-Bois", version = version))
        assertThat(stops).contains(Stop(_id = "72516", name = "Champêtre / du Sous-Bois", version = version))
        assertThat(stops).contains(Stop(_id = "72517", name = "Champêtre / du Sous-Bois", version = version))
        assertThat(stops).contains(Stop(_id = "72518", name = "Champêtre / du Sous-Bois", version = version))
        assertThat(stops).contains(Stop(_id = "72519", name = "Sainte-Angélique / Albert-Beaulne", version = version))
        assertThat(stops).contains(Stop(_id = "72520", name = "Sainte-Angélique / Gosselin", version = version))
        assertThat(stops).contains(Stop(_id = "72521", name = "Sainte-Angélique / du Grand-Pré", version = version))
        assertThat(stops).contains(Stop(_id = "72522", name = "Sainte-Angélique / Aberdeen", version = version))
        assertThat(stops).contains(Stop(_id = "72523", name = "Frontenac / Sainte-Angélique", version = version))
        assertThat(stops).contains(Stop(_id = "72524", name = "Sainte-Angélique / des Libellules", version = version))
        assertThat(stops).contains(Stop(_id = "72525", name = "Montcalm / Frontenac", version = version))
        assertThat(stops).contains(Stop(_id = "72526", name = "Frontenac / Montcalm", version = version))
        assertThat(stops).contains(Stop(_id = "72527", name = "Radisson / Montcalm", version = version))
        assertThat(stops).contains(Stop(_id = "72528", name = "Montcalm / Radisson", version = version))
        assertThat(stops).contains(Stop(_id = "72529", name = "Éric / Radisson", version = version))
        assertThat(stops).contains(Stop(_id = "72530", name = "Radisson / Éric", version = version))
        assertThat(stops).contains(Stop(_id = "72531", name = "Dutchy / Croissant  Amy", version = version))
        assertThat(stops).contains(Stop(_id = "72532", name = "Dutchy / Croissant  Amy", version = version))
        assertThat(stops).contains(Stop(_id = "72533", name = "Dutchy / Saint-Louis", version = version))
        assertThat(stops).contains(Stop(_id = "72534", name = "Dutchy / Saint-Louis", version = version))
        assertThat(stops).contains(Stop(_id = "72535", name = "Saint-Louis / Duhamel", version = version))
        assertThat(stops).contains(Stop(_id = "72536", name = "Saint-Louis / Duhamel", version = version))
        assertThat(stops).contains(Stop(_id = "72537", name = "Saint-Louis / de la Cité-des-Jeunes", version = version))
        assertThat(stops).contains(Stop(_id = "72538", name = "Saint-Louis / de la Cité-des-Jeunes", version = version))
        assertThat(stops).contains(Stop(_id = "72539", name = "Master / du Polo Drive", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72540",
                name = "côte St-Charles / devant le #2662 (Ultramar)",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72541",
                name = "côte St-Charles / devant le #2681 (Tim Horton's)",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72542", name = "des Sables / Yearling", version = version))
        assertThat(stops).contains(Stop(_id = "72543", name = "Yearling / des Sables", version = version))
        assertThat(stops).contains(Stop(_id = "72544", name = "du Bordelais / Sandmere", version = version))
        assertThat(stops).contains(Stop(_id = "72545", name = "du Bordelais / Sandmere", version = version))
        assertThat(stops).contains(Stop(_id = "72546", name = "Master / Huntsmen", version = version))
        assertThat(stops).contains(Stop(_id = "72547", name = "des Sables / Croissant  du Sablier", version = version))
        assertThat(stops).contains(Stop(_id = "72548", name = "Master / du Kentucky", version = version))
        assertThat(stops).contains(Stop(_id = "72549", name = "Master / du Kentucky", version = version))
        assertThat(stops).contains(Stop(_id = "72550", name = "Pie-XII / Trudeau", version = version))
        assertThat(stops).contains(Stop(_id = "72551", name = "Pie-XII / Trudeau", version = version))
        assertThat(stops).contains(Stop(_id = "72552", name = "Valois / 1re Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72553", name = "Valois / 1re Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72554", name = "du Bicentenaire / Paul-Gérin-Lajoie", version = version))
        assertThat(stops).contains(Stop(_id = "72555", name = "Paul-Gérin-Lajoie / du Bicentenaire", version = version))
        assertThat(stops).contains(Stop(_id = "72556", name = "Lefebvre / Béique", version = version))
        assertThat(stops).contains(Stop(_id = "72557", name = "Béique / Lefebvre", version = version))
        assertThat(stops).contains(Stop(_id = "72558", name = "Béique / Querbes", version = version))
        assertThat(stops).contains(Stop(_id = "72559", name = "Béique / Querbes", version = version))
        assertThat(stops).contains(Stop(_id = "72560", name = "des Loisirs / Béique", version = version))
        assertThat(stops).contains(Stop(_id = "72561", name = "Béique / des Loisirs", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72562",
                name = "des Loisirs / devant le parc Ste-Trinité",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72563",
                name = "des Loisirs / face au parc Ste-Trinité",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72564", name = "des Loisirs / Saint-Charles", version = version))
        assertThat(stops).contains(Stop(_id = "72565", name = "Saint-Charles / des Loisirs", version = version))
        assertThat(stops).contains(Stop(_id = "72566", name = "Jean-Jacques-Bertrand / Jean-Lesage", version = version))
        assertThat(stops).contains(Stop(_id = "72567", name = "Jean-Jacques-Bertrand / Jean-Lesage", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72568",
                name = "Jean-Jacques-Bertrand / Alexandre-Taschereau",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72569",
                name = "René-Lévesque / Jean-Jacques-Bertrand",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72570",
                name = "Jean-Jacques-Bertrand / René-Lévesque",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72571", name = "Napoléon-Parent / Larivée", version = version))
        assertThat(stops).contains(Stop(_id = "72572", name = "Larivée / Napoléon-Parent", version = version))
        assertThat(stops).contains(Stop(_id = "72573", name = "Dutrisac / de la Cité-des-Jeunes", version = version))
        assertThat(stops).contains(Stop(_id = "72574", name = "Boileau / de la Cité-des-Jeunes", version = version))
        assertThat(stops).contains(Stop(_id = "72575", name = "Boileau / de la Cité-des-Jeunes", version = version))
        assertThat(stops).contains(Stop(_id = "72576", name = "Boileau / Lartigue", version = version))
        assertThat(stops).contains(Stop(_id = "72577", name = "Jean-Lesage / Daniel-Johnson", version = version))
        assertThat(stops).contains(Stop(_id = "72578", name = "Jean-Lesage / Daniel-Johnson", version = version))
        assertThat(stops).contains(Stop(_id = "72579", name = "Jean-Lesage / Alexandre-Taschereau", version = version))
        assertThat(stops).contains(Stop(_id = "72580", name = "Jean-Lesage / Alexandre-Taschereau", version = version))
        assertThat(stops).contains(Stop(_id = "72581", name = "Jean-Lesage / Jean-Jacques-Bertrand", version = version))
        assertThat(stops).contains(Stop(_id = "72582", name = "Jean-Jacques-Bertrand / Jean-Lesage", version = version))
        assertThat(stops).contains(Stop(_id = "72583", name = "Boileau / Lartigue", version = version))
        assertThat(stops).contains(Stop(_id = "72584", name = "Montée Cadieux / Boily", version = version))
        assertThat(stops).contains(Stop(_id = "72585", name = "Boily / Montée Cadieux", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72586",
                name = "Saint-Charles / devant le tunnel vers Valois",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72587", name = "Adèle / Saint-Charles", version = version))
        assertThat(stops).contains(Stop(_id = "72588", name = "Harwood / Acres", version = version))
        assertThat(stops).contains(Stop(_id = "72589", name = "Harwood / de Normandie", version = version))
        assertThat(stops).contains(Stop(_id = "72590", name = "Harwood / de Normandie", version = version))
        assertThat(stops).contains(Stop(_id = "72591", name = "Harwood / des Érables", version = version))
        assertThat(stops).contains(Stop(_id = "72592", name = "Harwood / des Érables", version = version))
        assertThat(stops).contains(Stop(_id = "72593", name = "Harwood / de la Seigneurie", version = version))
        assertThat(stops).contains(Stop(_id = "72594", name = "Harwood / Boisé", version = version))
        assertThat(stops).contains(Stop(_id = "72595", name = "Harwood / Dooley", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72596",
                name = "Jean-Jacques-Bertrand / Alexandre-Taschereau",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72597", name = "Bourget / du Gouverneur", version = version))
        assertThat(stops).contains(Stop(_id = "72598", name = "Bourget / du Gouverneur", version = version))
        assertThat(stops).contains(Stop(_id = "72599", name = "du Gouverneur / Bourget", version = version))
        assertThat(stops).contains(Stop(_id = "72600", name = "Hardwood / de la Fabrique", version = version))
        assertThat(stops).contains(Stop(_id = "72601", name = "Chicoine / Saint-Jean-Baptiste", version = version))
        assertThat(stops).contains(Stop(_id = "72602", name = "Saint-Jean-Baptiste / Chicoine", version = version))
        assertThat(stops).contains(Stop(_id = "72603", name = "des Narcisses / des Pivoines", version = version))
        assertThat(stops).contains(Stop(_id = "72604", name = "des Floralies / des Pivoines", version = version))
        assertThat(stops).contains(Stop(_id = "72605", name = "des Perce-Neige / des Floralies", version = version))
        assertThat(stops).contains(Stop(_id = "72606", name = "des Floralies / des Perce-Neige", version = version))
        assertThat(stops).contains(Stop(_id = "72607", name = "Chicoine / De Lotbinière", version = version))
        assertThat(stops).contains(Stop(_id = "72608", name = "Chicoine / De Lotbinière", version = version))
        assertThat(stops).contains(Stop(_id = "72609", name = "Chicoine / Bellemare", version = version))
        assertThat(stops).contains(Stop(_id = "72610", name = "Chicoine / Bellemare", version = version))
        assertThat(stops).contains(Stop(_id = "72611", name = "Chicoine / Ranger", version = version))
        assertThat(stops).contains(Stop(_id = "72612", name = "Chicoine / Ranger", version = version))
        assertThat(stops).contains(Stop(_id = "72613", name = "Ranger / Chicoine", version = version))
        assertThat(stops).contains(Stop(_id = "72614", name = "Valois / 8e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72615", name = "Valois / 8e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72616", name = "8e Avenue / Valois", version = version))
        assertThat(stops).contains(Stop(_id = "72617", name = "de la Gare / du Manoir", version = version))
        assertThat(stops).contains(Stop(_id = "72618", name = "de la Gare / du Manoir", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72619",
                name = "Chicoine / devant le passage piétonnier",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72620",
                name = "Chicoine / face au passage piétonnier",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72621", name = "de la Gare / Édouard-Lalonde (CLSC)", version = version))
        assertThat(stops).contains(Stop(_id = "72622", name = "Édouard-Lalonde / de la Gare", version = version))
        assertThat(stops).contains(Stop(_id = "72623", name = "de la Gare / Édouard-Lalonde", version = version))
        assertThat(stops).contains(Stop(_id = "72624", name = "Édouard-Lalonde / de la Gare", version = version))
        assertThat(stops).contains(Stop(_id = "72625", name = "Adélard-Godbout / Lomer-Gouin", version = version))
        assertThat(stops).contains(Stop(_id = "72626", name = "Adélard-Godbout / Paul-Sauvé", version = version))
        assertThat(stops).contains(Stop(_id = "72627", name = "Lafleur / des Patriotes", version = version))
        assertThat(stops).contains(Stop(_id = "72628", name = "Lafleur / des Patriotes", version = version))
        assertThat(stops).contains(Stop(_id = "72629", name = "Bourget / de la Canardière", version = version))
        assertThat(stops).contains(Stop(_id = "72630", name = "de la Canardière / des Siffleurs", version = version))
        assertThat(stops).contains(Stop(_id = "72631", name = "de la Canardière / des Siffleurs", version = version))
        assertThat(stops).contains(Stop(_id = "72632", name = "de la Canardière / des Morillons", version = version))
        assertThat(stops).contains(Stop(_id = "72633", name = "de la Canardière / des Morillons", version = version))
        assertThat(stops).contains(Stop(_id = "72634", name = "de la Canardière / des Siffleurs", version = version))
        assertThat(stops).contains(Stop(_id = "72635", name = "de la Canardière / des Siffleurs", version = version))
        assertThat(stops).contains(Stop(_id = "72636", name = "Lavoie / Dutrisac", version = version))
        assertThat(stops).contains(Stop(_id = "72637", name = "Lavoie / Dutrisac", version = version))
        assertThat(stops).contains(Stop(_id = "72638", name = "Pie-XII / Brown", version = version))
        assertThat(stops).contains(Stop(_id = "72639", name = "Brown / Pie-XII", version = version))
        assertThat(stops).contains(Stop(_id = "72640", name = "Pie-XII / Dicaire", version = version))
        assertThat(stops).contains(Stop(_id = "72641", name = "Pie-XII / Dicaire", version = version))
        assertThat(stops).contains(Stop(_id = "72642", name = "Pie-XII / Robert-Goyer", version = version))
        assertThat(stops).contains(Stop(_id = "72643", name = "Pie-XII / Robert-Goyer", version = version))
        assertThat(stops).contains(Stop(_id = "72644", name = "des Tuileries / des Abbesses", version = version))
        assertThat(stops).contains(Stop(_id = "72645", name = "des Abbesses / des Tuileries", version = version))
        assertThat(stops).contains(Stop(_id = "72646", name = "des Tuileries / de Villandry", version = version))
        assertThat(stops).contains(Stop(_id = "72647", name = "des Tuileries / de Villandry", version = version))
        assertThat(stops).contains(Stop(_id = "72648", name = "de Chenonceau / des Tuileries", version = version))
        assertThat(stops).contains(Stop(_id = "72649", name = "des Tuileries / de Chenonceau", version = version))
        assertThat(stops).contains(Stop(_id = "72650", name = "Valois / Monette", version = version))
        assertThat(stops).contains(Stop(_id = "72651", name = "Valois / Monette", version = version))
        assertThat(stops).contains(Stop(_id = "72652", name = "Archambault / face au #580", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72653",
                name = "Archambault / face au passage piétonnier",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72654", name = "Émile-Bouchard / Aurèle-Joliat", version = version))
        assertThat(stops).contains(Stop(_id = "72655", name = "Aurèle-Joliat / Émile-Bouchard", version = version))
        assertThat(stops).contains(Stop(_id = "72656", name = "Émile-Bouchard / Howie-Morenz", version = version))
        assertThat(stops).contains(Stop(_id = "72657", name = "Émile-Bouchard / Howie-Morenz", version = version))
        assertThat(stops).contains(Stop(_id = "72658", name = "Émile-Bouchard / Toe-Blake", version = version))
        assertThat(stops).contains(Stop(_id = "72659", name = "Émile-Bouchard / Toe-Blake", version = version))
        assertThat(stops).contains(Stop(_id = "72660", name = "Jacques-Plante / Émile-Bouchard", version = version))
        assertThat(stops).contains(Stop(_id = "72661", name = "Émile-Bouchard / Jacques-Plante", version = version))
        assertThat(stops).contains(Stop(_id = "72662", name = "Pilon / Lafleur", version = version))
        assertThat(stops).contains(Stop(_id = "72663", name = "Lafleur / Pilon", version = version))
        assertThat(stops).contains(Stop(_id = "72664", name = "des Dahlias / des Crocus", version = version))
        assertThat(stops).contains(Stop(_id = "72665", name = "des Dahlias / des Oeillets", version = version))
        assertThat(stops).contains(Stop(_id = "72666", name = "des Oeillets / des Dahlias", version = version))
        assertThat(stops).contains(Stop(_id = "72667", name = "des Oeillets / des Lupins", version = version))
        assertThat(stops).contains(Stop(_id = "72668", name = "des Lupins / des Oeillets", version = version))
        assertThat(stops).contains(Stop(_id = "72669", name = "des Lupins / des Perce-Neige", version = version))
        assertThat(stops).contains(Stop(_id = "72670", name = "des Perce-Neige / des Lupins", version = version))
        assertThat(stops).contains(Stop(_id = "72671", name = "des Roseraies / des Perce-Neige", version = version))
        assertThat(stops).contains(Stop(_id = "72672", name = "des Perce-Neige / des Roseraies", version = version))
        assertThat(stops).contains(Stop(_id = "72673", name = "des Perce-Neige / des Roseraies", version = version))
        assertThat(stops).contains(Stop(_id = "72674", name = "Ouimet / Karine", version = version))
        assertThat(stops).contains(Stop(_id = "72675", name = "Ouimet / Karine", version = version))
        assertThat(stops).contains(Stop(_id = "72676", name = "1e Boulevard / 3e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72677", name = "3e Avenue / 1e Boulevard", version = version))
        assertThat(stops).contains(Stop(_id = "72678", name = "1e Boulevard / 7e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72679", name = "Ouimet / Florence", version = version))
        assertThat(stops).contains(Stop(_id = "72680", name = "Ouimet / Florence", version = version))
        assertThat(stops).contains(Stop(_id = "72681", name = "Émile-Bouchard / Maurice-Richard", version = version))
        assertThat(stops).contains(Stop(_id = "72682", name = "Émile-Bouchard / Maurice-Richard", version = version))
        assertThat(stops).contains(Stop(_id = "72683", name = "du Bicentenaire / Lefebvre", version = version))
        assertThat(stops).contains(Stop(_id = "72684", name = "du Bicentenaire / Lefebvre", version = version))
        assertThat(stops).contains(Stop(_id = "72685", name = "Paul-Gérin-Lajoie / des Muguets", version = version))
        assertThat(stops).contains(Stop(_id = "72686", name = "Paul-Gérin-Lajoie / des Muguets", version = version))
        assertThat(stops).contains(Stop(_id = "72687", name = "Paul-Gérin-Lajoie / Justine-Poirier", version = version))
        assertThat(stops).contains(Stop(_id = "72688", name = "Paul-Gérin-Lajoie / Justine-Poirier", version = version))
        assertThat(stops).contains(Stop(_id = "72689", name = "Sainte-Madeleine / Esther-Blondin", version = version))
        assertThat(stops).contains(Stop(_id = "72690", name = "Esther-Blondin / Sainte-Madeleine", version = version))
        assertThat(stops).contains(Stop(_id = "72691", name = "Esther-Blondin / Bourget", version = version))
        assertThat(stops).contains(Stop(_id = "72692", name = "Esther-Blondin / Bourget", version = version))
        assertThat(stops).contains(Stop(_id = "72693", name = "Bourget / Esther-Blondin", version = version))
        assertThat(stops).contains(Stop(_id = "72694", name = "Bourget / Esther-Blondin", version = version))
        assertThat(stops).contains(Stop(_id = "72695", name = "Joseph-Carrier / Adrien-Patenaude", version = version))
        assertThat(stops).contains(Stop(_id = "72696", name = "Joseph-Carrier / Adrien-Patenaude", version = version))
        assertThat(stops).contains(Stop(_id = "72697", name = "Joseph-Carrier / Aimé-Vincent", version = version))
        assertThat(stops).contains(Stop(_id = "72698", name = "Joseph-Carrier / Aimé-Vincent", version = version))
        assertThat(stops).contains(Stop(_id = "72699", name = "Bourget / Pilon", version = version))
        assertThat(stops).contains(Stop(_id = "72700", name = "Pilon / Bourget", version = version))
        assertThat(stops).contains(Stop(_id = "72701", name = "Joseph-Carrier / John-Desjardins", version = version))
        assertThat(stops).contains(Stop(_id = "72702", name = "Joseph-Carrier / John-Desjardins", version = version))
        assertThat(stops).contains(Stop(_id = "72703", name = "Joseph-Carrier / F.-X.-Tessier", version = version))
        assertThat(stops).contains(Stop(_id = "72704", name = "Joseph-Carrier / F.-X.-Tessier", version = version))
        assertThat(stops).contains(Stop(_id = "72705", name = "Joseph-Carrier / Marie-Curie", version = version))
        assertThat(stops).contains(Stop(_id = "72706", name = "Marie-Curie / Joseph-Carrier", version = version))
        assertThat(stops).contains(Stop(_id = "72707", name = "Marie-Curie / devant le #340", version = version))
        assertThat(stops).contains(Stop(_id = "72708", name = "Marie-Curie / devant le #341", version = version))
        assertThat(stops).contains(Stop(_id = "72709", name = "Marie-Curie / F.-X.-Tessier", version = version))
        assertThat(stops).contains(Stop(_id = "72710", name = "Marie-Curie / F.-X.-Tessier", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72711",
                name = "de la Gare / face au #3200 (Édifice Harden)",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72712",
                name = "de la Gare / devant Les Avenues Vaudreuil (Zibo)",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72713", name = "Bourget / Lafleur", version = version))
        assertThat(stops).contains(Stop(_id = "72714", name = "Lafleur / Bourget", version = version))
        assertThat(stops).contains(Stop(_id = "72715", name = "Archambault / devant le #496", version = version))
        assertThat(stops).contains(Stop(_id = "72716", name = "Archambault / devant le #495", version = version))
        assertThat(stops).contains(Stop(_id = "72717", name = "Bourget / Quévillon", version = version))
        assertThat(stops).contains(Stop(_id = "72718", name = "Bourget / Quévillon", version = version))
        assertThat(stops).contains(Stop(_id = "72719", name = "Larivée / Adélard-Godbout", version = version))
        assertThat(stops).contains(Stop(_id = "72720", name = "Adélard-Godbout / Larivée", version = version))
        assertThat(stops).contains(Stop(_id = "72721", name = "Adélard-Godbout / devant le #2784", version = version))
        assertThat(stops).contains(Stop(_id = "72722", name = "Adélard-Godbout / devant le #2783", version = version))
        assertThat(stops).contains(Stop(_id = "72723", name = "Bourget / Saint-Charles", version = version))
        assertThat(stops).contains(Stop(_id = "72724", name = "Saint-Charles / Bourget", version = version))
        assertThat(stops).contains(Stop(_id = "72725", name = "Bourget / Saint-Charles", version = version))
        assertThat(stops).contains(Stop(_id = "72726", name = "Saint-Charles / Bourget", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72727",
                name = "Saint-Charles / face au #1000 (Complexe Future)",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72728",
                name = "Saint-Charles / devant le #1000 (Complexe Future)",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72729", name = "De Tonnancour / Guillemette", version = version))
        assertThat(stops).contains(Stop(_id = "72730", name = "Guillemette / Chaput", version = version))
        assertThat(stops).contains(Stop(_id = "72731", name = "Guillemette / Lachapelle", version = version))
        assertThat(stops).contains(Stop(_id = "72735", name = "2e Avenue / du Curé-David", version = version))
        assertThat(stops).contains(Stop(_id = "72736", name = "2e Avenue / du Curé-David", version = version))
        assertThat(stops).contains(Stop(_id = "72737", name = "Harwood / De Lotbinière", version = version))
        assertThat(stops).contains(Stop(_id = "72738", name = "Harwood / De Lotbinière", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72739",
                name = "de la Cité-des-Jeunes / devant la Plaza Vaudreuil",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72740", name = "Saint-Charles / Brodeur", version = version))
        assertThat(stops).contains(Stop(_id = "72741", name = "Brodeur / Saint-Charles", version = version))
        assertThat(stops).contains(Stop(_id = "72742", name = "Saint-Charles / Brodeur", version = version))
        assertThat(stops).contains(Stop(_id = "72744", name = "Valois / André-Chartrand", version = version))
        assertThat(stops).contains(Stop(_id = "72745", name = "Valois / André-Chartrand", version = version))
        assertThat(stops).contains(Stop(_id = "72746", name = "André-Chartrand / Valois", version = version))
        assertThat(stops).contains(Stop(_id = "72748", name = "de la Gare / Forbes", version = version))
        assertThat(stops).contains(Stop(_id = "72749", name = "Forbes / de la Gare", version = version))
        assertThat(stops).contains(Stop(_id = "72750", name = "de la Gare / Forbes", version = version))
        assertThat(stops).contains(Stop(_id = "72751", name = "Sainte-Madeleine / Saint-Charles", version = version))
        assertThat(stops).contains(Stop(_id = "72752", name = "Saint-Charles / Sainte-Madeleine", version = version))
        assertThat(stops).contains(Stop(_id = "72753", name = "Saint-Charles / Sainte-Madeleine", version = version))
        assertThat(stops).contains(Stop(_id = "72754", name = "des Crocus / devant le #148", version = version))
        assertThat(stops).contains(Stop(_id = "72755", name = "des Crocus / des Roseraies", version = version))
        assertThat(stops).contains(Stop(_id = "72756", name = "Toe-Blake / Émile-Bouchard", version = version))
        assertThat(stops).contains(Stop(_id = "72757", name = "Émile-Bouchard / Toe-Blake", version = version))
        assertThat(stops).contains(Stop(_id = "72758", name = "Henry-Ford / des Méandres", version = version))
        assertThat(stops).contains(Stop(_id = "72759", name = "Henry-Ford / des Méandres", version = version))
        assertThat(stops).contains(Stop(_id = "72760", name = "des Méandres / du Ruisselet", version = version))
        assertThat(stops).contains(Stop(_id = "72761", name = "des Méandres / du Ruisselet", version = version))
        assertThat(stops).contains(Stop(_id = "72762", name = "des Méandres / du Torrent", version = version))
        assertThat(stops).contains(Stop(_id = "72763", name = "des Méandres / du Torrent", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72764",
                name = "des Méandres / devant le  passage piétonnier",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72765",
                name = "des Méandres / face au passage piétonnier",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72766", name = "du Ruisselet / des Méandres", version = version))
        assertThat(stops).contains(Stop(_id = "72767", name = "des Méandres / du Ruisselet", version = version))
        assertThat(stops).contains(Stop(_id = "72768", name = "du Ruisselet / des Rapides", version = version))
        assertThat(stops).contains(Stop(_id = "72769", name = "du Ruisselet / des Rapides", version = version))
        assertThat(stops).contains(Stop(_id = "72770", name = "du Ruisselet / des Dahlias", version = version))
        assertThat(stops).contains(Stop(_id = "72771", name = "des Dahlias / du Ruisselet", version = version))
        assertThat(stops).contains(Stop(_id = "72772", name = "des Amarantes / des Géraniums", version = version))
        assertThat(stops).contains(Stop(_id = "72773", name = "des Amarantes / des Géraniums", version = version))
        assertThat(stops).contains(Stop(_id = "72774", name = "des Clématites / des Amarantes", version = version))
        assertThat(stops).contains(Stop(_id = "72775", name = "des Amarantes / des Clématites", version = version))
        assertThat(stops).contains(Stop(_id = "72776", name = "des Clématites / des Géraniums", version = version))
        assertThat(stops).contains(Stop(_id = "72777", name = "des Clématites / des Géraniums", version = version))
        assertThat(stops).contains(Stop(_id = "72778", name = "Bill-Durnan / Jean-Claude-Tremblay", version = version))
        assertThat(stops).contains(Stop(_id = "72779", name = "Jean-Claude-Tremblay / Bill-Durnan", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72780",
                name = "Lorne-Worsley / Jean-Claude-Tremblay",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72781",
                name = "Jean-Claude-Tremblay / Lorne-Worsley",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72782",
                name = "Lorne-Worsley / Jean-Claude-Tremblay",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72783",
                name = "Lorne-Worsley / Jean-Claude-Tremblay",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72784", name = "Aurèle-Joliat / Lorne-Worsley", version = version))
        assertThat(stops).contains(Stop(_id = "72785", name = "Lorne-Worsley / Aurèle-Joliat", version = version))
        assertThat(stops).contains(Stop(_id = "72786", name = "Aurèle-Joliat / Maurice-Richard", version = version))
        assertThat(stops).contains(Stop(_id = "72787", name = "Aurèle-Joliat / Maurice-Richard", version = version))
        assertThat(stops).contains(Stop(_id = "72788", name = "Napoléon-Parent / Larivée", version = version))
        assertThat(stops).contains(Stop(_id = "72789", name = "Larivée / Napoléon-Parent", version = version))
        assertThat(stops).contains(Stop(_id = "72790", name = "Aurèle-Joliat / Toe-Blake", version = version))
        assertThat(stops).contains(Stop(_id = "72791", name = "Aurèle-Joliat / Toe-Blake", version = version))
        assertThat(stops).contains(Stop(_id = "72792", name = "Lorne-Worsley / devant le #242", version = version))
        assertThat(stops).contains(Stop(_id = "72793", name = "Lorne-Worsley / devant le #245", version = version))
        assertThat(stops).contains(Stop(_id = "72794", name = "des Floralies / des Jonquilles", version = version))
        assertThat(stops).contains(Stop(_id = "72795", name = "Dumberry / des Berges", version = version))
        assertThat(stops).contains(Stop(_id = "72796", name = "Dumberry / des Berges", version = version))
        assertThat(stops).contains(Stop(_id = "72797", name = "Dumberry / du Beaujolais", version = version))
        assertThat(stops).contains(Stop(_id = "72798", name = "Dumberry / du Beaujolais", version = version))
        assertThat(stops).contains(Stop(_id = "72799", name = "Dumberry / des Chenaux", version = version))
        assertThat(stops).contains(Stop(_id = "72800", name = "Boily / Larivée", version = version))
        assertThat(stops).contains(Stop(_id = "72801", name = "Larivée / Boily", version = version))
        assertThat(stops).contains(Stop(_id = "72802", name = "autoroute 40-O sortie 36 / Dumberry", version = version))
        assertThat(stops).contains(Stop(_id = "72803", name = "Dumberry / face au #21919", version = version))
        assertThat(stops).contains(Stop(_id = "72804", name = "Dumberry / devant le #21919", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72805",
                name = "de la Cité-des-Jeunes / Autoroute 40 Est",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72806", name = "Harwood / des Sources", version = version))
        assertThat(stops).contains(Stop(_id = "72807", name = "Harwood / des Sources", version = version))
        assertThat(stops).contains(Stop(_id = "72808", name = "Harwood / Séguin", version = version))
        assertThat(stops).contains(Stop(_id = "72809", name = "Harwood / Montée d'Alstonvale", version = version))
        assertThat(stops).contains(Stop(_id = "72810", name = "Harwood / Montée d'Alstonvale", version = version))
        assertThat(stops).contains(Stop(_id = "72811", name = "Harwood / Bernard", version = version))
        assertThat(stops).contains(Stop(_id = "72812", name = "Harwood / Major", version = version))
        assertThat(stops).contains(Stop(_id = "72813", name = "Harwood / de Val-des-Pins", version = version))
        assertThat(stops).contains(Stop(_id = "72814", name = "Harwood / Cambridge", version = version))
        assertThat(stops).contains(Stop(_id = "72815", name = "Harwood / des Dunes", version = version))
        assertThat(stops).contains(Stop(_id = "72816", name = "Harwood / devant le Pétro-Canada", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72817",
                name = "Harwood / devant le Centre d'achats Hudson",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72818", name = "Saint-Charles / Adam", version = version))
        assertThat(stops).contains(Stop(_id = "72819", name = "Saint-Charles / Adam", version = version))
        assertThat(stops).contains(Stop(_id = "72820", name = "Dutrisac / au passage piétonnier", version = version))
        assertThat(stops).contains(Stop(_id = "72821", name = "Dutrisac / au passage piétonnier", version = version))
        assertThat(stops).contains(Stop(_id = "72822", name = "Valois / De Tonnancour", version = version))
        assertThat(stops).contains(Stop(_id = "72823", name = "Valois / De Tonnancour", version = version))
        assertThat(stops).contains(Stop(_id = "72824", name = "Saint-Charles / Dumberry", version = version))
        assertThat(stops).contains(Stop(_id = "72825", name = "Saint-Charles / Dumberry", version = version))
        assertThat(stops).contains(Stop(_id = "72826", name = "Saint-Charles / Dutrisac", version = version))
        assertThat(stops).contains(Stop(_id = "72827", name = "Saint-Charles / Dutrisac", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72828",
                name = "Joseph-Carrier / devant la Place Carrier",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72829", name = "Joseph-Carrier / devant le #111", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72831",
                name = "Valois / face au tunnel vers Saint-Charles",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72832", name = "Charbonneau / Valois", version = version))
        assertThat(stops).contains(Stop(_id = "72833", name = "Charbonneau / Valois", version = version))
        assertThat(stops).contains(Stop(_id = "72835", name = "De Tonnancour / Desmarchais", version = version))
        assertThat(stops).contains(Stop(_id = "72837", name = "Chopin / André-Chartrand", version = version))
        assertThat(stops).contains(Stop(_id = "72838", name = "André-Chartrand / du Moulin", version = version))
        assertThat(stops).contains(Stop(_id = "72839", name = "Jeannotte / Bourget", version = version))
        assertThat(stops).contains(Stop(_id = "72840", name = "Jeannotte / Bourget", version = version))
        assertThat(stops).contains(Stop(_id = "72841", name = "Harwood / des Saules", version = version))
        assertThat(stops).contains(Stop(_id = "72842", name = "Harwood / des Saules", version = version))
        assertThat(stops).contains(Stop(_id = "72843", name = "de la Gare / devant le Métro", version = version))
        assertThat(stops).contains(Stop(_id = "72844", name = "de la Gare / devant le A&W", version = version))
        assertThat(stops).contains(Stop(_id = "72845", name = "Jacques-Plante / Maurice-Richard", version = version))
        assertThat(stops).contains(Stop(_id = "72846", name = "Jacques-Plante / Maurice-Richard", version = version))
        assertThat(stops).contains(Stop(_id = "72847", name = "Jacques-Plante / Toe-Blake", version = version))
        assertThat(stops).contains(Stop(_id = "72848", name = "Toe-Blake / Jacques-Plante", version = version))
        assertThat(stops).contains(Stop(_id = "72849", name = "de la Cité-des-Jeunes / Jeannotte", version = version))
        assertThat(stops).contains(Stop(_id = "72850", name = "Jeannotte / de la Cité-des-Jeunes", version = version))
        assertThat(stops).contains(Stop(_id = "72851", name = "des Dahlias / des Nénuphars", version = version))
        assertThat(stops).contains(Stop(_id = "72852", name = "des Dahlias / des Nénuphars", version = version))
        assertThat(stops).contains(Stop(_id = "72853", name = "Bourget / Lavoie", version = version))
        assertThat(stops).contains(Stop(_id = "72854", name = "Lavoie / Bourget", version = version))
        assertThat(stops).contains(Stop(_id = "72855", name = "des Sarcelles / des Becs-Scie", version = version))
        assertThat(stops).contains(Stop(_id = "72856", name = "des Becs-Scie / des Sarcelles", version = version))
        assertThat(stops).contains(Stop(_id = "72857", name = "des Sarcelles / devant le #2050", version = version))
        assertThat(stops).contains(Stop(_id = "72858", name = "Ouimet / de la Cité-des-Jeunes", version = version))
        assertThat(stops).contains(Stop(_id = "72859", name = "Ouimet / de la Cité-des-Jeunes", version = version))
        assertThat(stops).contains(Stop(_id = "72860", name = "Bourget / de la Cité-des-Jeunes", version = version))
        assertThat(stops).contains(Stop(_id = "72861", name = "Pilon / devant le #454", version = version))
        assertThat(stops).contains(Stop(_id = "72862", name = "Pilon / devant le #455", version = version))
        assertThat(stops).contains(Stop(_id = "72863", name = "Pilon / devant le #382", version = version))
        assertThat(stops).contains(Stop(_id = "72864", name = "Pilon / face au #382", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72865",
                name = "Jeannotte / devant l'Aréna de Vaudreuil",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72866",
                name = "Jeannotte / devant l'École Saint-Michel",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72867", name = "Ouimet / Leclerc", version = version))
        assertThat(stops).contains(Stop(_id = "72868", name = "Ouimet / Leclerc", version = version))
        assertThat(stops).contains(Stop(_id = "72871", name = "Marier / De Tonnancour", version = version))
        assertThat(stops).contains(Stop(_id = "72872", name = "Marier / De Tonnancour", version = version))
        assertThat(stops).contains(Stop(_id = "72873", name = "Marier / Schubert", version = version))
        assertThat(stops).contains(Stop(_id = "72874", name = "Marier / Schubert", version = version))
        assertThat(stops).contains(Stop(_id = "72875", name = "Marier / devant le #1041", version = version))
        assertThat(stops).contains(Stop(_id = "72876", name = "Marier / devant le #1040", version = version))
        assertThat(stops).contains(Stop(_id = "72878", name = "Ouimet / Briand", version = version))
        assertThat(stops).contains(Stop(_id = "72879", name = "Briand / Ouimet", version = version))
        assertThat(stops).contains(Stop(_id = "72880", name = "Ouimet / Briand", version = version))
        assertThat(stops).contains(Stop(_id = "72881", name = "8e Avenue / devant le #185", version = version))
        assertThat(stops).contains(Stop(_id = "72882", name = "8e Avenue / devant le #192", version = version))
        assertThat(stops).contains(Stop(_id = "72883", name = "Marier / Bellini", version = version))
        assertThat(stops).contains(Stop(_id = "72884", name = "Marier / Beethoven", version = version))
        assertThat(stops).contains(Stop(_id = "72885", name = "Marier / André-Chartrand", version = version))
        assertThat(stops).contains(Stop(_id = "72886", name = "Marier / André-Chartrand", version = version))
        assertThat(stops).contains(Stop(_id = "72887", name = "Marier / des Tilleuls", version = version))
        assertThat(stops).contains(Stop(_id = "72888", name = "Marier / des Merisiers", version = version))
        assertThat(stops).contains(Stop(_id = "72889", name = "Marier / des Noisetiers", version = version))
        assertThat(stops).contains(Stop(_id = "72890", name = "Marier / des Noisetiers", version = version))
        assertThat(stops).contains(Stop(_id = "72891", name = "Marier / des Merisiers", version = version))
        assertThat(stops).contains(Stop(_id = "72892", name = "Marier / des Merisiers", version = version))
        assertThat(stops).contains(Stop(_id = "72893", name = "2e Avenue / Marier", version = version))
        assertThat(stops).contains(Stop(_id = "72894", name = "Marier / 2e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72895", name = "Pinault / Ouimet", version = version))
        assertThat(stops).contains(Stop(_id = "72896", name = "Ouimet / Pinault", version = version))
        assertThat(stops).contains(Stop(_id = "72897", name = "Valois / Loyola-Schmidt", version = version))
        assertThat(stops).contains(Stop(_id = "72898", name = "Valois / Loyola-Schmidt", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72899",
                name = "Harwood / devant la Caisse Desjardins",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72901", name = "F.-X.-Tessier  / face au #4000", version = version))
        assertThat(stops).contains(Stop(_id = "72902", name = "F.-X.-Tessier  / devant le #4000", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72903",
                name = "de la Cité-des-Jeunes / devant le Holiday Inn",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72904",
                name = "de la Cité-des-Jeunes / devant le #21",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72905",
                name = "rue Becs-Scie / face au passage piétonnier",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72906", name = "Saint-Charles / Sainte-Angélique", version = version))
        assertThat(stops).contains(Stop(_id = "72907", name = "Saint-Charles / Sainte-Angélique", version = version))
        assertThat(stops).contains(Stop(_id = "72908", name = "Saint-Charles / Sainte-Angélique", version = version))
        assertThat(stops).contains(Stop(_id = "72909", name = "Dutrisac / Saint-Charles", version = version))
        assertThat(stops).contains(Stop(_id = "72910", name = "Dutrisac / Saint-Charles", version = version))
        assertThat(stops).contains(Stop(_id = "72911", name = "Boileau / Forbes", version = version))
        assertThat(stops).contains(Stop(_id = "72912", name = "Forbes / Boileau", version = version))
        assertThat(stops).contains(Stop(_id = "72913", name = "Boileau / Forbes", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72914",
                name = "de la Cité-des-Jeunes / devant l'Équipeur",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72915", name = "F.-X.-Tessier  / face au #3825", version = version))
        assertThat(stops).contains(Stop(_id = "72916", name = "F.-X.-Tessier  / devant le #3825", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72917",
                name = "de la Fabrique / devant l'École Papillon-Bleu",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72918", name = "Larivée / Marquis", version = version))
        assertThat(stops).contains(Stop(_id = "72919", name = "Larivée / Marquis", version = version))
        assertThat(stops).contains(Stop(_id = "72920", name = "Chicoine / Rang Saint-Antoine", version = version))
        assertThat(stops).contains(Stop(_id = "72921", name = "Rang Saint-Antoine / Chicoine", version = version))
        assertThat(stops).contains(Stop(_id = "72922", name = "Valois / 4e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72923", name = "Valois / 4e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72924", name = "4e Avenue / Valois", version = version))
        assertThat(stops).contains(Stop(_id = "72925", name = "4e Avenue / Valois", version = version))
        assertThat(stops).contains(Stop(_id = "72926", name = "Rang Saint-Antoine / Harwood", version = version))
        assertThat(stops).contains(Stop(_id = "72927", name = "Rang Saint-Antoine / Harwood", version = version))
        assertThat(stops).contains(Stop(_id = "72928", name = "Durocher / Bourget", version = version))
        assertThat(stops).contains(Stop(_id = "72929", name = "Bourget / Durocher", version = version))
        assertThat(stops).contains(Stop(_id = "72930", name = "Bourget / Durocher", version = version))
        assertThat(stops).contains(Stop(_id = "72931", name = "Briand / Lartigue", version = version))
        assertThat(stops).contains(Stop(_id = "72932", name = "Briand / Lartigue", version = version))
        assertThat(stops).contains(Stop(_id = "72933", name = "Valois / 2e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72934", name = "Valois / 2e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72935", name = "Valois / 2e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72936", name = "2e Avenue / Valois", version = version))
        assertThat(stops).contains(Stop(_id = "72937", name = "de la Cité-des-Jeunes / Briand", version = version))
        assertThat(stops).contains(Stop(_id = "72938", name = "de la Cité-des-Jeunes / Briand", version = version))
        assertThat(stops).contains(Stop(_id = "72939", name = "des Floralies / des Lys", version = version))
        assertThat(stops).contains(Stop(_id = "72940", name = "des Floralies / des Iris", version = version))
        assertThat(stops).contains(Stop(_id = "72941", name = "des Floralies / des Perce-Neige", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72942",
                name = "des Floralies / de la Cité-des-Jeunes",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72943",
                name = "de la Cité-des-Jeunes / des Floralies",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72944",
                name = "de la Cité-des-Jeunes / Marc-Aurèle-Fortin",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72945",
                name = "de la Cité-des-Jeunes / Marc-Aurèle-Fortin",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72946", name = "Gare Dorion", version = version))
        assertThat(stops).contains(Stop(_id = "72947", name = "Ouimet / Isabelle", version = version))
        assertThat(stops).contains(Stop(_id = "72948", name = "Ouimet / Isabelle", version = version))
        assertThat(stops).contains(Stop(_id = "72949", name = "des Pivoines / des Narcisses", version = version))
        assertThat(stops).contains(Stop(_id = "72950", name = "des Pivoines / devant le #2623", version = version))
        assertThat(stops).contains(Stop(_id = "72951", name = "Briand / Bastien", version = version))
        assertThat(stops).contains(Stop(_id = "72952", name = "Briand / Bastien", version = version))
        assertThat(stops).contains(Stop(_id = "72953", name = "Boul. Perrot / Boul. Saint-Joseph", version = version))
        assertThat(stops).contains(Stop(_id = "72954", name = "Virginie-Roy / Pasteur", version = version))
        assertThat(stops).contains(Stop(_id = "72955", name = "Virginie-Roy / de la Rivelaine", version = version))
        assertThat(stops).contains(Stop(_id = "72956", name = "25e Avenue / Boischatel", version = version))
        assertThat(stops).contains(Stop(_id = "72957", name = "25e Avenue / des Érables", version = version))
        assertThat(stops).contains(Stop(_id = "72958", name = "Virginie-Roy /  Perrier", version = version))
        assertThat(stops).contains(
            Stop(
                _id = "72960",
                name = "de la Gare / devant le Onze de la Gare",
                version = version
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "72961",
                name = "de la Gare / face au Onze de la Gare",
                version = version
            )
        )
        assertThat(stops).contains(Stop(_id = "72962", name = "3e Avenue / 42e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72963", name = "3e Avenue / 42e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72966", name = "Émile-Bouchard / Phil-Goyette", version = version))
        assertThat(stops).contains(Stop(_id = "72967", name = "Émile-Bouchard / Phil-Goyette", version = version))
        assertThat(stops).contains(Stop(_id = "72968", name = "Saint-Charles / Joseph-Carrier", version = version))
        assertThat(stops).contains(Stop(_id = "72969", name = "Virginie-Roy / Saint-Joseph", version = version))
        assertThat(stops).contains(Stop(_id = "72970", name = "Harwood / du Meunier", version = version))
        assertThat(stops).contains(Stop(_id = "72971", name = "Harwood / du Meunier", version = version))
        assertThat(stops).contains(Stop(_id = "72972", name = "Harwood / du Meunier", version = version))
        assertThat(stops).contains(Stop(_id = "72973", name = "Harwood / Harwood", version = version))
        assertThat(stops).contains(Stop(_id = "72975", name = "1e Boulevard / 7e Avenue", version = version))
        assertThat(stops).contains(Stop(_id = "72977", name = "Cameron / Cavagnal", version = version))
        assertThat(stops).contains(Stop(_id = "72978", name = "Terminus Vaudreuil Quai 2", version = version))
        assertThat(stops).contains(Stop(_id = "72979", name = "Terminus Vaudreuil Quai 3", version = version))
        assertThat(stops).contains(Stop(_id = "72980", name = "Terminus Vaudreuil Quai 4", version = version))
        assertThat(stops).contains(Stop(_id = "72981", name = "Terminus Vaudreuil Quai 5", version = version))
        assertThat(stops).contains(Stop(_id = "72982", name = "Terminus Vaudreuil Quai 6", version = version))
        assertThat(stops).contains(Stop(_id = "72983", name = "Terminus Vaudreuil Quai 7", version = version))
        assertThat(stops).contains(Stop(_id = "72984", name = "Terminus Vaudreuil Quai 8", version = version))
        assertThat(stops).contains(Stop(_id = "72985", name = "Terminus Vaudreuil Quai 9", version = version))
        assertThat(stops).contains(Stop(_id = "72986", name = "Terminus Vaudreuil Quai 10", version = version))
        assertThat(stops).contains(Stop(_id = "72987", name = "Terminus Vaudreuil Quai 11", version = version))
        assertThat(stops).contains(Stop(_id = "72988", name = "Terminus Vaudreuil Quai 12", version = version))
        assertThat(stops).contains(Stop(_id = "72989", name = "Terminus Vaudreuil Quai 13", version = version))
        assertThat(stops).contains(Stop(_id = "72990", name = "Cégep Gérald-Godin", version = version))
        assertThat(stops).contains(Stop(_id = "72991", name = "Saint-Jean / Shakespeare", version = version))
        assertThat(stops).contains(Stop(_id = "72992", name = "Saint-Jean / Roger-Pilon", version = version))
        assertThat(stops).contains(Stop(_id = "72993", name = "Dumberry / Besner", version = version))
        assertThat(stops).contains(Stop(_id = "72994", name = "Dumberry / Besner", version = version))
        assertThat(stops).contains(Stop(_id = "72995", name = "44e Avenue / 2e Boulevard", version = version))
        assertThat(stops).contains(Stop(_id = "72996", name = "44e Avenue / 2e Boulevard", version = version))
        assertThat(stops).contains(Stop(_id = "72997", name = "Gare Île-Perrot Quai 2", version = version))
        assertThat(stops).contains(Stop(_id = "72998", name = "Gare Île-Perrot Quai 1", version = version))
    }

    private fun importedAllTrips(version: String) {
        val trips = scheduledTripRepository.findAll()

        assertThat(trips).contains(
            ScheduledTrip(
                _id = "3281905-PI-A22-PI_GTFS-Semaine-01",
                routeID = "1",
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
                routeID = "115",
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
                routeID = "115",
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
                routeID = "115",
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
        scheduledStopRepository.deleteAll()
        scheduledTripRepository.deleteAll()
        stopRepository.deleteAll()
        routeRepository.deleteAll()
        agencyRepository.deleteAll()
        feedVersionRepository.deleteAll()
        feedRepository.deleteAll()
    }
}