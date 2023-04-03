package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.route.Route
import kotlinx.serialization.SerializationException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import java.io.File
import java.io.IOException
import java.nio.file.Path

@SpringBootTest
internal class GTFSRouteDataSourceTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @Test
    fun fileNotFound(@TempDir root: Path) {
        val version = "v1"
        val subject = GTFSRouteDataSource()

        val result = subject.routes(root.toString(), version).exceptionOrNull()!!

        assertThat(result).isInstanceOf(IOException::class.java)
    }

    @Test
    fun corrupted(@TempDir root: Path) {
        val inputStream = resourceLoader.getResource("classpath:citpi-routes-corrupt.txt").file.inputStream()
        val outputStream = File(root.toFile(), "routes.txt").outputStream()
        inputStream.copyTo(outputStream, 1024)
        val version = "v1"
        val subject = GTFSRouteDataSource()

        val result = subject.routes(root.toString(), version).exceptionOrNull()

        assertThat(result).isInstanceOf(SerializationException::class.java)
    }

    @Test
    fun importsSuccessfully(@TempDir root: Path) {
        val inputStream = resourceLoader.getResource("classpath:citpi-routes.txt").file.inputStream()
        val outputStream = File(root.toFile(), "routes.txt").outputStream()
        inputStream.copyTo(outputStream, 1024)
        val version = "v1"
        val subject = GTFSRouteDataSource()

        val routes = subject.routes(root.toString(), version).getOrNull()!!

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
                _id = "T1",
                shortName = "T1",
                longName = "Gare Vaudreuil/Parc Industriel/Seigneurie",
                agencyID = "CITPI",
                version = version,
                headwayHistory = emptyList()
            )
        )
    }
}
