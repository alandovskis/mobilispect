package com.mobilispect.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

private val AGENCY_A = Agency(_id = "o-abcd-a", "A")
private val AGENCY_B = Agency(_id = "o-abcd-b", "B")

class AgencyAPIIntegrationTest : APIIntegrationTest() {
    @Autowired
    private lateinit var agencyRepository: AgencyRepository

    @Test
    fun showsDataForAllAgencies() {
        agencyRepository.save(AGENCY_A)
        agencyRepository.save(AGENCY_B)

        val response: ResponseEntity<String> = template.getForEntity("/agencies", String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains(""""name" : "${AGENCY_A.name}""")
        assertThat(response.body).contains(""""name" : "${AGENCY_B.name}""")
    }

    @AfterEach
    fun cleanUp() {
        agencyRepository.deleteAll()
    }
}
