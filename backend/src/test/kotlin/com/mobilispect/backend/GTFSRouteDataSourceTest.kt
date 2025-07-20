package com.mobilispect.backend

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
        val result = subject.routes(root, VERSION, FEED_ID).exceptionOrNull()!!

        assertThat(result).isInstanceOf(IOException::class.java)
    }

    @Test
    fun corrupted(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:gtfs/routes/corrupt.txt", root = root, dst = "routes.txt")

        val result = subject.routes(root, VERSION, FEED_ID).exceptionOrNull()

        assertThat(result).isInstanceOf(SerializationException::class.java)
    }

    @Test
    fun importFailsIfMissingAgencyID(@TempDir root: Path) {
        resourceLoader.copyResourceTo(
            src = "classpath:gtfs/routes/missing-agency-id.txt",
            root = root,
            dst = "routes.txt"
        )

        val routes = subject.routes(root, VERSION, FEED_ID).exceptionOrNull()

        assertThat(routes).isNotNull()
    }

    @Test
    fun importsSuccessfullyWithAgencyID(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:gtfs/routes/full.txt", root = root, dst = "routes.txt")

        val routes = subject.routes(root, VERSION, FEED_ID).getOrNull()!!

        assertThat(routes).contains(
            Route(
                id = null,
                uid = "r-f2566-1",
                localID = "1",
                shortName = "1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = "o-f256-exo~citlapresquîle",
                versions = listOf(VERSION)
            )
        )

        assertThat(routes).contains(
            Route(
                id = null,
                uid = "r-f2566-t1",
                localID = "T1",
                shortName = "T1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = "o-f256-exo~citlapresquîle",
                versions = listOf(VERSION)
            )
        )
    }

    class TestAgencyIDDataSource : AgencyIDDataSource {
        override fun agencyIDs(feedID: String): Result<Map<String, String>> =
            Result.success(mapOf("CITPI" to "o-f256-exo~citlapresquîle"))
    }
}
