package com.mobilispect.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = [
	"com.mobilispect.backend.schedule.agency",
	"com.mobilispect.backend.schedule.feed",
	"com.mobilispect.backend.schedule.transit_land.api",
	"com.mobilispect.backend.schedule.transit_land.internal.client",
	"com.mobilispect.backend.schedule.transit_land.internal.credentials",
])
class BackendApplication

fun main(args: Array<String>) {
	runApplication<BackendApplication>(*args)
}
