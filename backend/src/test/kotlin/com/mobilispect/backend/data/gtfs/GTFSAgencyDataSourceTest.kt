package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.agency.Agency
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

    private val subject = GTFSAgencyDataSource()

    @Test
    fun fileNotFound(@TempDir root: Path) {
        val result = subject.agencies(root.toString(), VERSION).exceptionOrNull()!!

        assertThat(result).isInstanceOf(IOException::class.java)
    }

    @Test
    fun corrupted(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-agency-corrupt.txt", root = root, dst = "agency.txt")

        val result = subject.agencies(root.toString(), VERSION).exceptionOrNull()

        assertThat(result).isInstanceOf(SerializationException::class.java)
    }

    @Test
    fun importsSuccessfully(@TempDir root: Path) {
        resourceLoader.copyResourceTo(src = "classpath:citpi-agency.txt", root = root, dst = "agency.txt")

        val agencies = subject.agencies(root.toString(), VERSION).getOrNull()!!

        assertThat(agencies).contains(
            Agency(_id = "CITPI", name = "exo-La Presqu'île", version = VERSION)
        )
    }
}
