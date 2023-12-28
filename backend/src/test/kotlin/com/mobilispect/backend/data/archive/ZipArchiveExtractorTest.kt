package com.mobilispect.backend.data.archive

import com.mobilispect.backend.util.copyResourceTo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import java.io.IOException
import java.nio.file.Path
import java.security.MessageDigest

@SpringBootTest
internal class ZipArchiveExtractorTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @Test
    fun missingFile() {
        val subject = ZipArchiveExtractor()

        val result = subject.extract(Path.of("not-found.zip")).exceptionOrNull()!!

        assertThat(result).isInstanceOf(IOException::class.java)
    }

    @Test
    fun unzips(@TempDir root: Path) {
        val archive = resourceLoader.copyResourceTo(
            src = "classpath:exopi-gtfs-d89aa5de884111e4b6a9365220ded9f746ef2dbf.zip",
            root = root,
            dst = "target.zip"
        )

        val subject = ZipArchiveExtractor()
        val result = subject.extract(archive).getOrNull()!!

        assertThat(result.resolve("feed_info.txt")).hasDigest(
            MessageDigest.getInstance("SHA-1"),
            "90f845cc85604c16b42de467d75a0f248cd74f8b"
        )
        assertThat(result.resolve("agency.txt")).hasDigest(
            MessageDigest.getInstance("SHA-1"),
            "1933405c2a63c50b2208ad4f5630dcba1cc3bfa4"
        )
        assertThat(result.resolve("routes.txt")).hasDigest(
            MessageDigest.getInstance("SHA-1"),
            "e6cc3c8afd4e14a31c720ad83b2b400d67f80826"
        )
        assertThat(result.resolve("stops.txt")).hasDigest(
            MessageDigest.getInstance("SHA-1"),
            "f26305ab152213ee406d000e2708a8ac44e4140a"
        )
        assertThat(result.resolve("trips.txt")).hasDigest(
            MessageDigest.getInstance("SHA-1"),
            "72de62d45d75f344819c56e87f1b21d2ecb2b055"
        )
        assertThat(result.resolve("stop_times.txt")).hasDigest(
            MessageDigest.getInstance("SHA-1"),
            "40cc8c891aa1502efcf86a4698e164720563d2db"
        )
        assertThat(result.resolve("calendar.txt")).hasDigest(
            MessageDigest.getInstance("SHA-1"),
            "5ba0c9f94f998cde8b4ed1918f284a7b979f0cd0"
        )
        assertThat(result.resolve("calendar_dates.txt")).hasDigest(
            MessageDigest.getInstance("SHA-1"),
            "bcd4914812b99b8808c4a5dd2a9cbb1a10eb7235"
        )
        assertThat(result.resolve("shapes.txt")).hasDigest(
            MessageDigest.getInstance("SHA-1"),
            "cc4e2b906ad52be0a2788c38cadc15fd19f77110"
        )
        assertThat(result.resolve("fare_attributes.txt")).hasDigest(
            MessageDigest.getInstance("SHA-1"),
            "b8356dce4795bbeed12fd8fe019beae791785157"
        )
        assertThat(result.resolve("fare_rules.txt")).hasDigest(
            MessageDigest.getInstance("SHA-1"),
            "21903ac7cad3bd905ae3dc5391d38d0442d0bf32"
        )
    }
}
