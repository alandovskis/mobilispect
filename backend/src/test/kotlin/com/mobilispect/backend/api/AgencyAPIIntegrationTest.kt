package com.mobilispect.backend.api

import com.mobilispect.backend.data.MongoDBInitializer
import com.mobilispect.backend.data.agency.Agency
import com.mobilispect.backend.data.agency.AgencyRepository
import com.mobilispect.backend.data.agency.OneStopAgencyID
import com.mobilispect.backend.data.createMongoDBContainer
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

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [AgencyAPIIntegrationTest.Companion.DBInitializer::class])
@Testcontainers
class AgencyAPIIntegrationTest {
    companion object {
        @Container
        @JvmStatic
        val container = createMongoDBContainer()

        class DBInitializer : MongoDBInitializer(container)
    }

    @Autowired
    private lateinit var template: TestRestTemplate

    @Autowired
    private lateinit var agencyRepository: AgencyRepository

    @Test
    fun showsDataForAllAgencies() {
        agencyRepository.save(AGENCY_A)
        agencyRepository.save(AGENCY_B)

        val response = template.getForEntity("/agencies", String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains(""""name" : "${AGENCY_A.name}""")
        assertThat(response.body).contains(""""name" : "${AGENCY_B.name}""")
    }

    @AfterEach
    fun cleanUp() {
        agencyRepository.deleteAll()
    }
}
