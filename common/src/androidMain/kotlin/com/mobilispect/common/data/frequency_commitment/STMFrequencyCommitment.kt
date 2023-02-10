package com.mobilispect.common.data.frequency_commitment

import com.mobilispect.common.data.agency.STM_ID
import com.mobilispect.common.data.schedule.Direction
import com.mobilispect.common.data.time.WEEKDAYS
import java.time.Duration
import java.time.LocalTime

// Source: https://www.stm.info/en/info/networks/bus/local/10-minutes-max
private val bothDirections = FrequencyCommitmentItem(
    daysOfWeek = WEEKDAYS,
    routes = listOf(
        "r-f25ej-18",
        "r-f25dv-24",
        "r-f25em-141"
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
        "r-f25e-33",
        "r-f25df-64",
        "r-f25ds-103",
        "r-f25dk-106",
        "r-f25dk-406"
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
        "r-f25ej-18",
        "r-f25dv-24",
        "r-f25du-51",
        "r-f25ej-67",
        "r-f25e-69",
        "r-f25dv-80",
        "r-f25ds-105",
        "r-f25e5-121",
        "r-f25ej-139",
        "r-f25em-141",
        "r-f25du-165"
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
        "r-f25em-32",
        "r-f25e-33",
        "r-f25em-44",
        "r-f25e-45",
        "r-f25e-48",
        "r-f25e-49",
        "r-f25e-55",
        "r-f25df-64",
        "r-f25ds-90",
        "r-f25ej-97",
        "r-f25ds-103",
        "r-f25dk-106",
        "r-f25em-136",
        "r-f25du-161",
        "r-f25dg-171",
        "r-f25ex-187",
        "r-f25ej-193",
        "r-f25ej-197",
        "r-f25dk-406",
        "r-f256-470"
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
