package com.mobilispect.backend

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


private val AGENCY_A = Agency(_id = "o-abcd-a", "A")
private val AGENCY_B = Agency(_id = "o-abcd-b", "B")

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [AgencyAPIIntegrationTest.Companion.MongoDBInitializer::class])
@Testcontainers
class AgencyAPIIntegrationTest {
    companion object {
        @Container
        @JvmStatic
        val container = MongoDBContainer(DockerImageName.parse("mongo:6.0.3"))

        class MongoDBInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(applicationContext: ConfigurableApplicationContext) {
                val values = TestPropertyValues.of(
                    "spring.data.mongodb.host=" + container.host,
                    "spring.data.mongodb.port=" + container.firstMappedPort
                )
                values.applyTo(applicationContext)
            }

        }
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
