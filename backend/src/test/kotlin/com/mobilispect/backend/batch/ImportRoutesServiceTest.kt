package com.mobilispect.backend.batch

import com.mobilispect.backend.data.MongoDBInitializer
import com.mobilispect.backend.data.createMongoDBContainer
import com.mobilispect.backend.data.route.ExportedRouteRepository
import com.mobilispect.backend.data.route.FakeRouteDataSource
import com.mobilispect.backend.data.route.UnexportedRouteRepository
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
@ContextConfiguration(initializers = [ImportRoutesServiceTest.Companion.DBInitializer::class])
@Testcontainers
class ImportRoutesServiceTest {
    companion object {
        @Container
        @JvmStatic
        val container = createMongoDBContainer()

        class DBInitializer : MongoDBInitializer(container)
    }

    @Autowired
    private lateinit var exportedRouteRepository: ExportedRouteRepository

    @Autowired
    private lateinit var unexportedRouteRepository: UnexportedRouteRepository

    private val networkDataSource: FakeRouteDataSource = FakeRouteDataSource()

    private lateinit var subject: ImportRoutesService

    @BeforeEach
    fun prepare() {
        subject = ImportRoutesService(
            exportedRouteRepository,
            unexportedRouteRepository,
            networkDataSource,
            FakeTransitLandCredentialsRepository()
        )
    }

    @Test
    fun addsAllMissingAgenciesWhenThereAreNone() {
        val expected = networkDataSource.all()

        subject.apply(agencyID = "agencyID")

        val actual = unexportedRouteRepository.findAll()
        assertThat(actual).containsAll(expected)
    }

    @Test
    fun addsAnyMissingAgencies() {
        val expected = FakeRouteDataSource().routes(apiKey = "apikey", agencyID = "agencyID").getOrNull()!!
        unexportedRouteRepository.save(expected.routes.first().copy())

        subject.apply(agencyID = "agencyID")

        val actual = unexportedRouteRepository.findAll()
        assertThat(actual).containsAll(expected.routes)
    }

    @Test
    fun doesNothingWhenAllAgenciesArePresentAndLatestVersion() {
        val expected = FakeRouteDataSource().routes(apiKey = "apikey", agencyID = "agencyID").getOrNull()!!
        for (agency in expected.routes) {
            unexportedRouteRepository.save(agency.copy())
        }

        subject.apply(agencyID = "agencyID")

        val actual = unexportedRouteRepository.findAll()
        assertThat(actual).containsAll(expected.routes)
    }

    @Test
    fun updatesAgencyWhenAllAgenciesArePresentButNotLatestVersion() {
        val expected = FakeRouteDataSource().routes(apiKey = "apikey", agencyID = "agencyID").getOrNull()!!
        for (agency in expected.routes) {
            unexportedRouteRepository.save(agency.copy(version = "old"))
        }

        subject.apply(agencyID = "agencyID")

        val actual = unexportedRouteRepository.findAll()
        assertThat(actual).containsAll(expected.routes)
    }
}