package com.mobilispect.backend.api

import com.mobilispect.backend.MongoDBInitializer
import com.mobilispect.backend.Agency
import com.mobilispect.backend.createMongoDBContainer
import com.mobilispect.backend.schedule.Route
import com.mobilispect.backend.schedule.RouteRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


private val AGENCY_A =
    Agency(uid = "o-abcd-a", localID = "ABCD-A", name = "A", versions = listOf("v1"))
private val AGENCY_B =
    Agency(uid = "o-abcd-b", localID = "ABCD-B", name = "B", versions = listOf("v2"))

private val ROUTE_A1 = Route(
    uid = "r-abcd-1",
    localID = "A1",
    shortName = "1",
    longName = "Main Street",
    agencyID = AGENCY_A.uid,
    versions = listOf("v2"),
)
private val ROUTE_A2 = Route(
    uid = "r-abcd-2",
    localID = "2",
    shortName = "2",
    longName = "Central Avenue",
    agencyID = AGENCY_A.uid,
    versions = listOf("v1"),
)
private val ROUTE_B1 = Route(
    uid = "r-cdef-1",
    localID = "1",
    shortName = "1",
    longName = "1st Street",
    agencyID = AGENCY_B.uid,
    versions = listOf("v2"),
)

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [RouteAPIIntegrationTest.Companion.DBInitializer::class])
@Testcontainers
class RouteAPIIntegrationTest {
    companion object {
        @Container
        @JvmStatic
        val container = createMongoDBContainer()

        class DBInitializer : MongoDBInitializer(container)
    }

    @Autowired
    private lateinit var template: TestRestTemplate

    @Autowired
    private lateinit var routeRepository: RouteRepository

    @Test
    fun showsRoutesForSpecifiedAgency() {
        routeRepository.save(ROUTE_A1)
        routeRepository.save(ROUTE_A2)
        routeRepository.save(ROUTE_B1)

        val response = template.getForEntity("/routes/search/findAllByAgencyID?id=${AGENCY_A.uid}", String::class.java)

        println(response.body)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val positive = listOf(ROUTE_A1, ROUTE_A2)
        for (route in positive) {
            assertThat(response.body).contains(""""shortName" : "${route.shortName}""")
            assertThat(response.body).contains(""""longName" : "${route.longName}""")
        }
        assertThat(response.body).doesNotContain(""""longName" : "${ROUTE_B1.longName}""")
    }

    @AfterEach
    fun cleanUp() {
        routeRepository.deleteAll()
    }
}
