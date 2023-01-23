package com.mobilispect.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
	runApplication<ApiApplication>(*args)
}
