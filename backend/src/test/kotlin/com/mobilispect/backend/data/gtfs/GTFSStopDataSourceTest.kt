package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.schedule.stop.Stop
import com.mobilispect.backend.schedule.stop.StopIDDataSource
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
internal class GTFSStopDataSourceTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    private val subject = GTFSStopDataSource(TestStopIDDataSource())

    @Test
    fun fileNotFound(@TempDir root: Path) {
        val result = subject.stops(root, VERSION, FEED_ID).exceptionOrNull()!!

        assertThat(result).isInstanceOf(IOException::class.java)
    }

    @Test
    fun corrupted(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-stops-corrupt.txt", root = root, dst = "stops.txt")

        val result = subject.stops(root, VERSION, FEED_ID).exceptionOrNull()

        assertThat(result).isInstanceOf(SerializationException::class.java)
    }

    @Test
    fun importsSuccessfully(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-stops.txt", root = root, dst = "stops.txt")

        val stops = subject.stops(root, VERSION, FEED_ID).getOrNull()!!

        assertThat(stops).contains(
            Stop(
                _id = "s-f256hrvf2g-1eboulevard~11eavenue",
                name = "1e Boulevard / 11e Avenue",
                version = VERSION
            )
        )
        assertThat(stops).contains(
            Stop(
                _id = "s-f256hrtws3-1eboulevard~11eavenue",
                name = "1e Boulevard / 11e Avenue",
                version = VERSION
            )
        )
    }
}

class TestStopIDDataSource : StopIDDataSource {
    private val stopIDMap = mutableMapOf<String, String>()

    init {
        stopIDMap["71998"] = "s-f256hrvf2g-1eboulevard~11eavenue"
        stopIDMap["71999"] = "s-f256hrtws3-1eboulevard~11eavenue"
    }

    override fun stops(feedID: String): Result<Map<String, String>> = Result.success(stopIDMap)
}
