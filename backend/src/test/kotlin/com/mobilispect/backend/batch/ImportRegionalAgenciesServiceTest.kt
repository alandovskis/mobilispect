package com.mobilispect.backend.batch

import com.mobilispect.backend.data.MongoDBInitializer
import com.mobilispect.backend.data.agency.AgencyRepository
import com.mobilispect.backend.data.agency.FakeRegionalAgencyDataSource
import com.mobilispect.backend.data.agency.RegionalAgencyDataSource
import com.mobilispect.backend.data.createMongoDBContainer
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
@ContextConfiguration(initializers = [ImportRegionalAgenciesServiceTest.Companion.DBInitializer::class])
@Testcontainers
class ImportRegionalAgenciesServiceTest {
    companion object {
        @Container
        @JvmStatic
        val container = createMongoDBContainer()

        class DBInitializer : MongoDBInitializer(container)
    }

    @Autowired
    private lateinit var agencyRepository: AgencyRepository

    private val networkDataSource: RegionalAgencyDataSource = FakeRegionalAgencyDataSource()

    private lateinit var subject: ImportRegionalAgenciesService

    @BeforeEach
    fun prepare() {
        subject =
            ImportRegionalAgenciesService(agencyRepository, networkDataSource, FakeTransitLandCredentialsRepository())
    }

    @Test
    fun addsAllMissingAgenciesWhenThereAreNone() {
        val expected = networkDataSource.agencies(apiKey = "apikey", city = "city").getOrNull()!!

        subject.apply("city")

        val actual = agencyRepository.findAll()
        assertThat(actual).containsAll(expected.agencies)
    }

    @Test
    fun addsAnyMissingAgencies() {
        val expected = networkDataSource.agencies(apiKey = "apikey", city = "city").getOrNull()!!
        agencyRepository.save(expected.agencies.first().copy())

        subject.apply("city")

        val actual = agencyRepository.findAll()
        assertThat(actual).containsAll(expected.agencies)
    }

    @Test
    fun doesNothingWhenAllAgenciesArePresentAndLatestVersion() {
        val expected = networkDataSource.agencies(apiKey = "apikey", city = "city").getOrNull()!!
        for (agency in expected.agencies) {
            agencyRepository.save(agency.copy())
        }

        subject.apply("city")

        val actual = agencyRepository.findAll()
        assertThat(actual).containsAll(expected.agencies)
    }

    @Test
    fun updatesAgencyWhenAllAgenciesArePresentButNotLatestVersion() {
        val expected = networkDataSource.agencies(apiKey = "apikey", city = "city").getOrNull()!!
        for (agency in expected.agencies) {
            agencyRepository.save(agency.copy(version = "old"))
        }

        subject.apply("city")

        val actual = agencyRepository.findAll()
        assertThat(actual).containsAll(expected.agencies)
    }
}
