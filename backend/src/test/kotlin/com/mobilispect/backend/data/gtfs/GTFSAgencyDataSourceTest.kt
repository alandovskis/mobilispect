package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.agency.Agency
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
internal class GTFSAgencyDataSourceTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @Test
    fun fileNotFound(@TempDir root: Path) {
        val version = "v1"
        val subject = GTFSAgencyDataSource()

        val result = subject.agencies(root.toString(), version).exceptionOrNull()!!

        assertThat(result).isInstanceOf(IOException::class.java)
    }

    @Test
    fun corrupted(@TempDir root: Path) {
        val inputStream = resourceLoader.getResource("classpath:citpi-agency-corrupt.txt").file.inputStream()
        val outputStream = File(root.toFile(), "agency.txt").outputStream()
        inputStream.copyTo(outputStream, 1024)
        val version = "v1"
        val subject = GTFSAgencyDataSource()

        val result = subject.agencies(root.toString(), version).exceptionOrNull()

        assertThat(result).isInstanceOf(SerializationException::class.java)
    }

    @Test
    fun importsSuccessfully(@TempDir root: Path) {
        val inputStream = resourceLoader.getResource("classpath:citpi-agency.txt").file.inputStream()
        val outputStream = File(root.toFile(), "agency.txt").outputStream()
        inputStream.copyTo(outputStream, 1024)
        val version = "v1"
        val subject = GTFSAgencyDataSource()

        val agencies = subject.agencies(root.toString(), version).getOrNull()!!

        assertThat(agencies).contains(
            Agency(_id = "CITPI", name = "exo-La Presqu'île", version = version)
        )
    }
}