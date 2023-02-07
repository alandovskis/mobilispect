package com.mobilispect.backend

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

private val AGENCY_A = Agency(_id = "o-abcd-a", "A")
private val AGENCY_B = Agency(_id = "o-abcd-b", "B")

private val ROUTE_A1 = Route("r-abcd-1", shortName = "1", "Main Street", agencyID = AGENCY_A._id)
private val ROUTE_A2 = Route("r-abcd-2", shortName = "2", "Central Avenue", agencyID = AGENCY_A._id)
private val ROUTE_B1 = Route("r-cdef-1", shortName = "1", "1st Street", agencyID = AGENCY_B._id)

class RouteAPIIntegrationTest : APIIntegrationTest() {
    @Autowired
    private lateinit var routeRepository: RouteRepository

    @Test
    fun showsRoutesForSpecifiedAgency() {
        routeRepository.save(ROUTE_A1)
        routeRepository.save(ROUTE_A2)
        routeRepository.save(ROUTE_B1)

        val response = template.getForEntity("/routes/search/findAllByAgencyID?id=${AGENCY_A._id}", String::class.java)

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
