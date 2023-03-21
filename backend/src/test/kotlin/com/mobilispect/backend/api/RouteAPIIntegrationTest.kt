package com.mobilispect.backend.api

import com.mobilispect.backend.data.MongoDBInitializer
import com.mobilispect.backend.data.agency.Agency
import com.mobilispect.backend.data.createMongoDBContainer
import com.mobilispect.backend.data.route.HeadwayEntry
import com.mobilispect.backend.data.route.Route
import com.mobilispect.backend.data.route.UnexportedRouteRepository
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


private val AGENCY_A = Agency(_id = "o-abcd-a", name = "A", version = "v1")
private val AGENCY_B = Agency(_id = "o-abcd-b", name = "B", version = "v2")

private val ROUTE_A1 = Route(
    _id = "r-abcd-1",
    shortName = "1",
    longName = "Main Street",
    agencyID = AGENCY_A._id,
    version = "v2]1",
    headwayHistory = listOf(
        HeadwayEntry(medianHeadway_min = 5.0)
    )
)
private val ROUTE_A2 = Route(
    _id = "r-abcd-2",
    shortName = "2",
    longName = "Central Avenue",
    agencyID = AGENCY_A._id,
    version = "v1",
    headwayHistory = listOf(
        HeadwayEntry(medianHeadway_min = 10.0)
    )
)
private val ROUTE_B1 = Route(
    _id = "r-cdef-1",
    shortName = "1",
    longName = "1st Street",
    agencyID = AGENCY_B._id,
    version = "v2",
    headwayHistory = emptyList()
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
    private lateinit var unexportedRouteRepository: UnexportedRouteRepository

    @Test
    fun showsRoutesForSpecifiedAgency() {
        unexportedRouteRepository.save(ROUTE_A1)
        unexportedRouteRepository.save(ROUTE_A2)
        unexportedRouteRepository.save(ROUTE_B1)

        val response = template.getForEntity("/routes/search/findAllByAgencyID?id=${AGENCY_A._id}", String::class.java)

        println(response.body)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val positive = listOf(ROUTE_A1, ROUTE_A2)
        for (route in positive) {
            assertThat(response.body).contains(""""shortName" : "${route.shortName}""")
            assertThat(response.body).contains(""""longName" : "${route.longName}""")
            assertThat(response.body).contains(
                """"medianHeadway_min" : ${
                    route.headwayHistory.firstOrNull()?.medianHeadway_min
                }"""
            )
        }
        assertThat(response.body).doesNotContain(""""longName" : "${ROUTE_B1.longName}""")
    }

    @AfterEach
    fun cleanUp() {
        unexportedRouteRepository.deleteAll()
    }
}
