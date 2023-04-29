package com.mobilispect.backend.api

import com.mobilispect.backend.data.MongoDBInitializer
import com.mobilispect.backend.data.agency.Agency
import com.mobilispect.backend.data.agency.OneStopAgencyID
import com.mobilispect.backend.data.createMongoDBContainer
import com.mobilispect.backend.data.route.OneStopRouteID
import com.mobilispect.backend.data.route.Route
import com.mobilispect.backend.data.route.RouteRepository
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


private val AGENCY_A = Agency(id = OneStopAgencyID("o-abcd-a"), name = "A", version = "v1")
private val AGENCY_B = Agency(id = OneStopAgencyID("o-abcd-b"), name = "B", version = "v2")

private val ROUTE_A1 = Route(
    id = OneStopRouteID("r-abcd-1"),
    shortName = "1",
    longName = "Main Street",
    agencyID = OneStopAgencyID(AGENCY_A._id),
    version = "v2"
)
private val ROUTE_A2 = Route(
    id = OneStopRouteID("r-abcd-2"),
    shortName = "2",
    longName = "Central Avenue",
    agencyID = OneStopAgencyID(AGENCY_A._id),
    version = "v1"
)
private val ROUTE_B1 = Route(
    id = OneStopRouteID("r-cdef-1"),
    shortName = "1",
    longName = "1st Street",
    agencyID = OneStopAgencyID(AGENCY_B._id),
    version = "v2"
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

        val response = template.getForEntity("/routes/search/findAllByAgencyID?id=${AGENCY_A._id}", String::class.java)

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
