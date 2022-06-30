package com.mobilispect.common.data.frequency

import com.mobilispect.common.data.agency.AgencyRef
import com.mobilispect.common.data.routes.RouteRef
import com.mobilispect.common.data.time.WEEKDAYS
import java.time.Duration
import java.time.LocalTime

val STM_ID = AgencyRef("o-f25d-socitdetransportdemontral")

// Source: https://www.stm.info/en/info/networks/bus/local/10-minutes-max
private val bothDirections = FrequencyCommitmentItem(
    daysOfWeek = WEEKDAYS,
    routes = listOf(
        RouteRef("f25ej-18"),
        RouteRef("f25dv-24"),
        RouteRef("f25em-141")),
    directions = DirectionTime.both(
        start = LocalTime.of(6, 0),
        end = LocalTime.of(21, 0),
    ),
    frequency = Duration.ofMinutes(10)
)

private val tidalFlow = FrequencyCommitmentItem(
    daysOfWeek = WEEKDAYS,
    routes = listOf(RouteRef("f25e-33"),
        RouteRef("f25df-64"),
        RouteRef("f25ds-103"),
        RouteRef("f25dk-106"),
        RouteRef("f25dk-406")),
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

val STM_FREQUENCY_COMMITMENT = FrequencyCommitment (
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
    routes = listOf(RouteRef("f25ej-18"), RouteRef("f25dv-24"),
        RouteRef("f25du-51"), RouteRef("f25ej-67"),
        RouteRef("f25e-69"), RouteRef("f25dv-80"),
        RouteRef("f25ds-105"), RouteRef("f25e5-121"),
        RouteRef("f25ej-139"), RouteRef("f25em-141"),
        RouteRef("f25du-165")),
    directions = DirectionTime.both(
        start = LocalTime.of(6, 0),
        end = LocalTime.of(21, 0),
    ),
    frequency = Duration.ofMinutes(10)
)

private val tidalFlowPast = FrequencyCommitmentItem(
    daysOfWeek = WEEKDAYS,
    routes = listOf(
        RouteRef("f25em-32"), RouteRef("f25e-33"), RouteRef("f25em-44"),
        RouteRef("f25e-45"), RouteRef("f25e-48"), RouteRef("f25e-49"),
        RouteRef("f25e-55"), RouteRef("f25df-64"), RouteRef("f25ds-90"),
        RouteRef("f25ej-97"), RouteRef("f25ds-103"), RouteRef("f25dk-106"),
        RouteRef("f25em-136"), RouteRef("f25du-161"), RouteRef("f25dg-171"),
        RouteRef("f25ex-187"), RouteRef("f25ej-193"), RouteRef("f25ej-197"),
        RouteRef("f25dk-406"), RouteRef("f256-470")),
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

val STM_FREQUENCY_COMMITMENT_PRE_COVID = FrequencyCommitment (
    spans = listOf(
        bothDirectionsPast,
        tidalFlowPast,
    ),
    agency = STM_ID
)
