package com.mobilispect.common.data.frequency

import com.mobilispect.common.data.agency.AgencyRef
import com.mobilispect.common.data.routes.RouteRef
import com.mobilispect.common.data.time.WEEKDAYS
import java.time.Duration
import java.time.LocalTime

val STM_ID = AgencyRef("f25d", agencyName = "socitdetransportdemontral")

// Source: https://www.stm.info/en/info/networks/bus/local/10-minutes-max
private val bothDirections = FrequencyCommitmentItem(
    daysOfWeek = WEEKDAYS,
    routes = listOf(
        RouteRef(geohash = "f25ej", routeNumber = "18"),
        RouteRef(geohash = "f25dv", routeNumber = "24"),
        RouteRef(geohash = "f25em", routeNumber = "141")
    ),
    directions = DirectionTime.both(
        start = LocalTime.of(6, 0),
        end = LocalTime.of(21, 0),
    ),
    frequency = Duration.ofMinutes(10)
)

private val tidalFlow = FrequencyCommitmentItem(
    daysOfWeek = WEEKDAYS,
    routes = listOf(
        RouteRef(geohash = "f25e", routeNumber = "33"),
        RouteRef(geohash = "f25df", routeNumber = "64"),
        RouteRef(geohash = "f25ds", routeNumber = "103"),
        RouteRef(geohash = "f25dk", routeNumber = "106"),
        RouteRef(geohash = "f25dk", routeNumber = "406")
    ),
    directions = listOf(
        DirectionTime(
            direction = Direction.Inbound,
            start = LocalTime.of(6, 0),
            end = LocalTime.of(14, 0),
        ),
        DirectionTime(
            direction = Direction.Outbound,
            start = LocalTime.of(14, 0),
            end = LocalTime.of(21, 0),
        ),
    ),
    frequency = Duration.ofMinutes(10)
)

val STM_FREQUENCY_COMMITMENT = FrequencyCommitment(
    spans = listOf(
        bothDirections,
        tidalFlow,
    ),
    agency = STM_ID
)

/**
 * Source:https://web.archive.org/web/20160709151109/http://www.stm.info:80/en/info/networks/bus/local/10-minutes-max
 */

private val bothDirectionsPast = FrequencyCommitmentItem(
    daysOfWeek = WEEKDAYS,
    routes = listOf(
        RouteRef(geohash = "f25ej", routeNumber = "18"),
        RouteRef(geohash = "f25dv", routeNumber = "24"),
        RouteRef(geohash = "f25du", routeNumber = "51"),
        RouteRef(geohash = "f25ej", routeNumber = "67"),
        RouteRef(geohash = "f25e", routeNumber = "69"),
        RouteRef(geohash = "f25dv", routeNumber = "80"),
        RouteRef(geohash = "f25ds", routeNumber = "105"),
        RouteRef(geohash = "f25e5", routeNumber = "121"),
        RouteRef(geohash = "f25ej", routeNumber = "139"),
        RouteRef(geohash = "f25em", routeNumber = "141"),
        RouteRef(geohash = "f25du", routeNumber = "165")
    ),
    directions = DirectionTime.both(
        start = LocalTime.of(6, 0),
        end = LocalTime.of(21, 0),
    ),
    frequency = Duration.ofMinutes(10)
)

private val tidalFlowPast = FrequencyCommitmentItem(
    daysOfWeek = WEEKDAYS,
    routes = listOf(
        RouteRef(geohash = "f25em", routeNumber = "32"),
        RouteRef(geohash = "f25e", routeNumber = "33"),
        RouteRef(geohash = "f25em", routeNumber = "44"),
        RouteRef(geohash = "f25e", routeNumber = "45"),
        RouteRef(geohash = "f25e", routeNumber = "48"),
        RouteRef(geohash = "f25e", routeNumber = "49"),
        RouteRef(geohash = "f25e", routeNumber = "55"),
        RouteRef(geohash = "f25df", routeNumber = "64"),
        RouteRef(geohash = "f25ds", routeNumber = "90"),
        RouteRef(geohash = "f25ej", routeNumber = "97"),
        RouteRef(geohash = "f25ds", routeNumber = "103"),
        RouteRef(geohash = "f25dk", routeNumber = "106"),
        RouteRef(geohash = "f25em", routeNumber = "136"),
        RouteRef(geohash = "f25du", routeNumber = "161"),
        RouteRef(geohash = "f25dg", routeNumber = "171"),
        RouteRef(geohash = "f25ex", routeNumber = "187"),
        RouteRef(geohash = "f25ej", routeNumber = "193"),
        RouteRef(geohash = "f25ej", routeNumber = "197"),
        RouteRef(geohash = "f25dk", routeNumber = "406"),
        RouteRef(geohash = "f256", routeNumber = "470")
    ),
    directions = listOf(
        DirectionTime(
            direction = Direction.Inbound,
            start = LocalTime.of(6, 0),
            end = LocalTime.of(14, 0),
        ),
        DirectionTime(
            direction = Direction.Outbound,
            start = LocalTime.of(14, 0),
            end = LocalTime.of(21, 0),
        ),
    ),
    frequency = Duration.ofMinutes(10)
)

val STM_FREQUENCY_COMMITMENT_PRE_COVID = FrequencyCommitment(
    spans = listOf(
        bothDirectionsPast,
        tidalFlowPast,
    ),
    agency = STM_ID
)
