package com.mobilispect.backend.batch

import com.mobilispect.backend.data.MongoDBInitializer
import com.mobilispect.backend.data.createMongoDBContainer
import com.mobilispect.backend.data.stop.FakeStopDataSource
import com.mobilispect.backend.data.stop.StopRepository
import com.mobilispect.backend.data.transit_land.FakeTransitLandCredentialsRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [ImportStopsServiceTest.Companion.DBInitializer::class])
@Testcontainers
class ImportStopsServiceTest {
    companion object {
        @Container
        @JvmStatic
        val container = createMongoDBContainer()

        class DBInitializer : MongoDBInitializer(container)
    }

    @Autowired
    private lateinit var stopRepository: StopRepository

    private val networkDataSource: FakeStopDataSource = FakeStopDataSource()

    private lateinit var subject: ImportStopsService

    @BeforeEach
    fun prepare() {
        subject = ImportStopsService(stopRepository, networkDataSource, FakeTransitLandCredentialsRepository())
    }

    @Test
    fun addsAllMissingStopsWhenThereAreNone() {
        val expected = networkDataSource.all()

        subject.apply(agencyID = "agencyID")

        val actual = stopRepository.findAll()
        assertThat(actual).containsAll(expected)
    }

    @Test
    fun addsAnyMissingStops() {
        val expected = FakeStopDataSource().stops(apiKey = "apikey", agencyID = "agencyID").getOrNull()!!
        stopRepository.save(expected.stops.first().copy())

        subject.apply(agencyID = "agencyID")

        val actual = stopRepository.findAll()
        assertThat(actual).containsAll(expected.stops)
    }

    @Test
    fun doesNothingWhenAllStopsArePresentAndLatestVersion() {
        val expected = FakeStopDataSource().stops(apiKey = "apikey", agencyID = "agencyID").getOrNull()!!
        for (agency in expected.stops) {
            stopRepository.save(agency.copy())
        }

        subject.apply(agencyID = "agencyID")

        val actual = stopRepository.findAll()
        assertThat(actual).containsAll(expected.stops)
    }

    @Test
    fun updatesStopWhenAllStopsArePresentButNotLatestVersion() {
        val expected = FakeStopDataSource().stops(apiKey = "apikey", agencyID = "agencyID").getOrNull()!!
        for (agency in expected.stops) {
            stopRepository.save(agency.copy(version = "old"))
        }

        subject.apply(agencyID = "agencyID")

        val actual = stopRepository.findAll()
        assertThat(actual).containsAll(expected.stops)
    }
}