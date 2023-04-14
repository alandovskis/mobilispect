package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.stop.Stop
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
internal class GTFSStopDataSourceTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    private val subject = GTFSStopDataSource()

    @Test
    fun fileNotFound(@TempDir root: Path) {
        val result = subject.stops(root.toString(), VERSION).exceptionOrNull()!!

        assertThat(result).isInstanceOf(IOException::class.java)
    }

    @Test
    fun corrupted(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-stops-corrupt.txt", root = root, dst = "stops.txt")

        val result = subject.stops(root.toString(), VERSION).exceptionOrNull()

        assertThat(result).isInstanceOf(SerializationException::class.java)
    }

    @Test
    fun importsSuccessfully(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-stops.txt", root = root, dst = "stops.txt")

        val stops = subject.stops(root.toString(), VERSION).getOrNull()!!

        assertThat(stops).contains(Stop(_id = "71998", name = "1e Boulevard / 11e Avenue", version = VERSION))
        assertThat(stops).contains(Stop(_id = "71999", name = "1e Boulevard / 11e Avenue", version = VERSION))
    }
}
