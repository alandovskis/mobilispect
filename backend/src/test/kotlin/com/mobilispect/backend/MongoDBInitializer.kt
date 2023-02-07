package com.mobilispect.backend

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

fun createMongoDBContainer() = MongoDBContainer(DockerImageName.parse("mongo:6.0.3"))

/**
 * An [ApplicationContextInitializer] that configures Spring Data MongoDB to use testcontainers instance.
 */
open class MongoDBInitializer(private val container: MongoDBContainer) :
    ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val values = TestPropertyValues.of(
            "spring.data.mongodb.host=" + container.host,
            "spring.data.mongodb.port=" + container.firstMappedPort
        )
        values.applyTo(applicationContext)
    }
}