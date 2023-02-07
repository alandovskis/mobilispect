package com.mobilispect.backend

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

/**
 * An integration test that exercises one of the APIs.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [APIIntegrationTest.Companion.MongoDBInitializer::class])
@Testcontainers
class APIIntegrationTest {
    @Autowired
    protected lateinit var template: TestRestTemplate

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
}

