package com.mobilispect.backend.data.gtfs

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

@SpringBootTest
internal class GTFSRouteDataSourceTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    private val subject = GTFSRouteDataSource()

    @Test
    fun fileNotFound(@TempDir root: Path) {
        val result = subject.routes(root.toString(), VERSION).exceptionOrNull()!!

        assertThat(result).isInstanceOf(IOException::class.java)
    }

    @Test
    fun corrupted(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-routes-corrupt.txt", root = root, dst = "routes.txt")

        val result = subject.routes(root.toString(), VERSION).exceptionOrNull()

        assertThat(result).isInstanceOf(SerializationException::class.java)
    }

    @Test
    fun importsSuccessfully(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-routes.txt", root = root, dst = "routes.txt")

        val routes = subject.routes(root.toString(), VERSION).getOrNull()!!

        assertThat(routes).contains(
            Route(
                _id = "1",
                shortName = "1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = "CITPI",
                version = VERSION,
                headwayHistory = emptyList()
            )
        )

        assertThat(routes).contains(
            Route(
                _id = "T1",
                shortName = "T1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = "CITPI",
                version = VERSION,
                headwayHistory = emptyList()
            )
        )
    }
}
