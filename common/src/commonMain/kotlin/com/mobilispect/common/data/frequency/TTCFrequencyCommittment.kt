package com.mobilispect.common.data.frequency

import com.mobilispect.common.data.agency.AgencyRef
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime

private val TTC_ID = AgencyRef(id = "o-dpz8-ttc")

// Source: https://ttc-cdn.azureedge.net/-/media/Project/TTC/DevProto/Images/Home/Routes-and-Schedules/Landing-page-pdfs/TTC_SystemMap_2021-11.pdf?rev=58232b2f280d4314b6435c79199af773
val item = FrequencyCommitmentItem(
    daysOfWeek = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
    ),
    frequency = Duration.ofMinutes(10),
    directions = DirectionTime.both(
        start = LocalTime.of(6, 0),
        end = LocalTime.of(1, 0)
    ),
    routes = listOf(
        "1",
        "2",
        "3",
        "4",
        "7",
        "22",
        "24",
        "25",
        "29",
        "32",
        "34",
        "35",
        "36",
        "39",
        "43",
        "44",
        "45",
        "47",
        "52",
        "53",
        "60",
        "63",
        "72",
        "76",
        "84",
        "85",
        "86",
        "87",
        "89",
        "94",
        "95",
        "96",
        "100",
        "102",
        "116",
        "129",
        "165",
        "501",
        "504",
        "505",
        "506",
        "509",
        "510",
        "511",
        "512",
        "900",
        "927",
    ))

val TTC_FREQUENCY_COMMITMENT = FrequencyCommitment(
    spans = listOf(item),
    agency = TTC_ID)