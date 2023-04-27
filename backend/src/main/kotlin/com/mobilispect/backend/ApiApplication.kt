package com.mobilispect.backend

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(
	info = Info(
		title = "Mobilispect API",
		description = "API Definitions for the Mobility Inspector",
		version = "1"
	)
)
class ApiApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
	runApplication<ApiApplication>(*args)
}
