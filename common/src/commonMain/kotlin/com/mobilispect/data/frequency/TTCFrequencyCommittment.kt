package com.mobilispect.data.frequency

import com.mobilispect.data.agency.AgencyRef
import com.mobilispect.data.routes.RouteRef
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime

private val TTC_ID = AgencyRef(geohash = "dpz8", agencyName = "ttc")

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
        RouteRef(geohash = "dpz8", routeNumber = "1"),
        RouteRef(geohash = "dpz8", routeNumber = "2"),
        RouteRef(geohash = "dpz8u", routeNumber = "3"),
        RouteRef(geohash = "dpz8c", routeNumber = "4"),
        RouteRef(geohash = "dpz8", routeNumber = "7"),
        RouteRef(geohash = "dpz87", routeNumber = "22"),
        RouteRef(geohash = "dpz8", routeNumber = "24"),
        RouteRef(geohash = "dpz8", routeNumber = "25"),
        RouteRef(geohash = "dpz8", routeNumber = "29"),
        RouteRef(geohash = "dpz2", routeNumber = "32"),
        RouteRef(geohash = "dpz8", routeNumber = "34"),
        RouteRef(geohash = "dpz2", routeNumber = "35"),
        RouteRef(geohash = "dpz2", routeNumber = "36"),
        RouteRef(geohash = "dpz9", routeNumber = "39"),
        RouteRef(geohash = "dpz95", routeNumber = "43"),
        RouteRef(geohash = "dpx2n", routeNumber = "44"),
        RouteRef(geohash = "dpz2t", routeNumber = "45"),
        RouteRef(geohash = "dpz2x", routeNumber = "47"),
        RouteRef(geohash = "dpz2", routeNumber = "52"),
        RouteRef(geohash = "dpz9", routeNumber = "53"),
        RouteRef(geohash = "dpz3", routeNumber = "60"),
        RouteRef(geohash = "dpz82", routeNumber = "63"),
        RouteRef(geohash = "dpz86", routeNumber = "72"),
        RouteRef(geohash = "dpz2n", routeNumber = "76"),
        RouteRef(geohash = "dpz2", routeNumber = "84"),
        RouteRef(geohash = "dpz9", routeNumber = "85"),
        RouteRef(geohash = "dpz8", routeNumber = "86"),
        RouteRef(geohash = "dpz8d", routeNumber = "87"),
        RouteRef(geohash = "dpz2x", routeNumber = "89"),
        RouteRef(geohash = "dpz83", routeNumber = "94"),
        RouteRef(geohash = "dpz8", routeNumber = "95"),
        RouteRef(geohash = "dpz2", routeNumber = "96"),
        RouteRef(geohash = "dpz8d", routeNumber = "100"),
        RouteRef(geohash = "dpz9", routeNumber = "102"),
        RouteRef(geohash = "dpz9j", routeNumber = "116"),
        RouteRef(geohash = "dpz9", routeNumber = "129"),
        RouteRef(geohash = "dpz2", routeNumber = "165"),
        RouteRef(geohash = "dpz8", routeNumber = "501"),
        RouteRef(geohash = "dpz83", routeNumber = "504"),
        RouteRef(geohash = "dpz8", routeNumber = "505"),
        RouteRef(geohash = "dpz8", routeNumber = "506"),
        RouteRef(geohash = "dpz83", routeNumber = "509"),
        RouteRef(geohash = "dpz83", routeNumber = "510"),
        RouteRef(geohash = "dpz83", routeNumber = "511"),
        RouteRef(geohash = "dpz82", routeNumber = "512"),
        RouteRef(geohash = "dpz2m", routeNumber = "900"),
        RouteRef(geohash = "dpz2", routeNumber = "927"),
    ))

val TTC_FREQUENCY_COMMITMENT = FrequencyCommitment(
    spans = listOf(item),
    agency = TTC_ID)