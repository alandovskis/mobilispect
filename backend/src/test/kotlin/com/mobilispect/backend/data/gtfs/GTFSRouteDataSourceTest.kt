package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.agency.AgencyIDDataSource
import com.mobilispect.backend.data.agency.FeedLocalAgencyID
import com.mobilispect.backend.data.agency.OneStopAgencyID
import com.mobilispect.backend.data.route.OneStopRouteID
import com.mobilispect.backend.data.route.Route
import com.mobilispect.backend.util.copyResourceTo
import kotlinx.serialization.SerializationException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import java.io.IOException
import java.nio.file.Path

private const val VERSION = "v1"
private const val FEED_ID = "feed_id"

@SpringBootTest
internal class GTFSRouteDataSourceTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    private val subject = GTFSRouteDataSource(TestAgencyIDDataSource(), TestRouteIDDataSource())

    @Test
    fun fileNotFound(@TempDir root: Path) {
        val result = subject.routes(root.toString(), VERSION, FEED_ID).exceptionOrNull()!!

        assertThat(result).isInstanceOf(IOException::class.java)
    }

    @Test
    fun corrupted(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-routes-corrupt.txt", root = root, dst = "routes.txt")

        val result = subject.routes(root.toString(), VERSION, FEED_ID).exceptionOrNull()

        assertThat(result).isInstanceOf(SerializationException::class.java)
    }

    @Test
    fun importsSuccessfullyEvenIfMissingAgencyID(@TempDir root: Path) {
        resourceLoader.copyResourceTo(
            src = "classpath:gtfs/routes/missing-agency-id.txt",
            root = root,
            dst = "routes.txt"
        )

        val routes = subject.routes(root.toString(), VERSION, FEED_ID).getOrNull()!!

        assertThat(routes).contains(
            Route(
                id = OneStopRouteID("r-f2566-1"),
                shortName = "1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = OneStopAgencyID("o-f256-exo~citlapresquîle"),
                version = VERSION
            )
        )

        assertThat(routes).contains(
            Route(
                id = OneStopRouteID("r-f2566-t1"),
                shortName = "T1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = OneStopAgencyID("o-f256-exo~citlapresquîle"),
                version = VERSION
            )
        )
    }

    @Test
    fun importsSuccessfullyWithAgencyID(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-routes.txt", root = root, dst = "routes.txt")

        val routes = subject.routes(root.toString(), VERSION, FEED_ID).getOrNull()!!

        assertThat(routes).contains(
            Route(
                id = OneStopRouteID("r-f2566-1"),
                shortName = "1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = OneStopAgencyID("o-f256-exo~citlapresquîle"),
                version = VERSION
            )
        )

        assertThat(routes).contains(
            Route(
                id = OneStopRouteID("r-f2566-t1"),
                shortName = "T1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = OneStopAgencyID("o-f256-exo~citlapresquîle"),
                version = VERSION
            )
        )
    }

    class TestAgencyIDDataSource : AgencyIDDataSource {
        override fun agencyIDs(feedID: String): Result<Map<FeedLocalAgencyID, OneStopAgencyID>> =
            Result.success(mapOf(FeedLocalAgencyID("CITPI") to OneStopAgencyID("o-f256-exo~citlapresquîle")))
    }
}
