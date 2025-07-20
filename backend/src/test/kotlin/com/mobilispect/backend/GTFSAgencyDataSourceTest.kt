package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.Agency
import com.mobilispect.backend.AgencyIDDataSource
import com.mobilispect.backend.GTFSAgencyDataSource
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
internal class GTFSAgencyDataSourceTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @Test
    fun fileNotFound(@TempDir root: Path) {
        val agencyIDDataSource = TestAgencyIDDataSource(emptyMap())
        val subject = GTFSAgencyDataSource(agencyIDDataSource)

        val result = subject.agencies(root, VERSION, "Montréal").exceptionOrNull()!!

        assertThat(result).isInstanceOf(IOException::class.java)
    }

    @Test
    fun corrupted(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-agency-corrupt.txt", root = root, dst = "agency.txt")
        val agencyIDDataSource = TestAgencyIDDataSource(emptyMap())
        val subject = GTFSAgencyDataSource(agencyIDDataSource)

        val result = subject.agencies(root, VERSION, "Montréal").exceptionOrNull()

        assertThat(result).isInstanceOf(SerializationException::class.java)
    }

    @Test
    fun missingOneStopID(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-agency.txt", root = root, dst = "agency.txt")
        val agencyIDDataSource = TestAgencyIDDataSource(
            mapOf(
                "STM" to "o-f25d-socitdetransportdemontral"
            )
        )
        val subject = GTFSAgencyDataSource(agencyIDDataSource)

        val agencies = subject.agencies(root, VERSION, "Montréal").getOrNull()!!

        assertThat(agencies).isEmpty()
    }

    @Test
    fun importsSuccessfully(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-agency.txt", root = root, dst = "agency.txt")
        val agencyIDDataSource = TestAgencyIDDataSource(
            mapOf(
                "CITPI" to "o-f256-exo~citlapresquîle",
                "STM" to "o-f25d-socitdetransportdemontral"
            )
        )
        val subject = GTFSAgencyDataSource(agencyIDDataSource)

        val agencies = subject.agencies(root, VERSION, "Montréal").getOrNull()!!

        assertThat(agencies).contains(
            Agency(
                uid = "o-f256-exo~citlapresquîle",
                name = "exo-La Presqu'île",
                localID = "CITPI",
                versions = listOf(VERSION)
            )
        )
    }

    class TestAgencyIDDataSource(private val pairs: Map<String, String>) :
        AgencyIDDataSource {
        override fun agencyIDs(feedID: String): Result<Map<String, String>> = Result.success(pairs)
    }
}
