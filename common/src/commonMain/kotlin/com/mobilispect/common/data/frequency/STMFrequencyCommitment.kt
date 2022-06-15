package com.mobilispect.common.data.frequency

import com.mobilispect.common.data.agency.AgencyRef
import com.mobilispect.common.data.time.WEEKDAYS
import java.time.Duration
import java.time.LocalTime

val STM_ID = AgencyRef("o-f25d-socitdetransportdemontral")

// Source: https://www.stm.info/en/info/networks/bus/local/10-minutes-max
private val bothDirections = FrequencyCommitmentItem(
    daysOfWeek = WEEKDAYS,
    routes = listOf("18", "24", "141"),
    directions = DirectionTime.both(
        start = LocalTime.of(6, 0),
        end = LocalTime.of(21, 0),
    ),
    frequency = Duration.ofMinutes(10)
)

private val tidalFlow = FrequencyCommitmentItem(
    daysOfWeek = WEEKDAYS,
    routes = listOf("33", "64", "103", "106", "406"),
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
    routes = listOf("18", "24", "51", "67", "69", "80", "105",
        "121", "139", "141", "165"),
    directions = DirectionTime.both(
        start = LocalTime.of(6, 0),
        end = LocalTime.of(21, 0),
    ),
    frequency = Duration.ofMinutes(10)
)

private val tidalFlowPast = FrequencyCommitmentItem(
    daysOfWeek = WEEKDAYS,
    routes = listOf("32", "33", "44", "45", "48", "49",
        "55", "64", "90", "97", "103", "106", "136", "161",
        "171", "187", "193", "197", "406", "470"),
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