package com.mobilispect.common.data.frequency

import com.mobilispect.common.data.agency.AgencyRef
import com.mobilispect.common.data.routes.RouteRef
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
        RouteRef("dpz8-1"),
        RouteRef("dpz8-2"),
        RouteRef("dpz8u-3"),
        RouteRef("dpz8c-4"),
        RouteRef("dpz8-7"),
        RouteRef("dpz87-22"),
        RouteRef("dpz8-24"),
        RouteRef("dpz8-25"),
        RouteRef("dpz8-29"),
        RouteRef("dpz2-32"),
        RouteRef("dpz8-34"),
        RouteRef("dpz2-35"),
        RouteRef("dpz2-36"),
        RouteRef("dpz9-39"),
        RouteRef("dpz95-43"),
        RouteRef("dpx2n-44"),
        RouteRef("dpz2t-45"),
        RouteRef("dpz2x-47"),
        RouteRef("dpz2-52"),
        RouteRef("dpz9-53"),
        RouteRef("dpz3-60"),
        RouteRef("dpz82-63"),
        RouteRef("dpz86-72"),
        RouteRef("dpz2n-76"),
        RouteRef("dpz2-84"),
        RouteRef("dpz9-85"),
        RouteRef("dpz8-86"),
        RouteRef("dpz8d-87"),
        RouteRef("dpz2x-89"),
        RouteRef("dpz83-94"),
        RouteRef("dpz8-95"),
        RouteRef("dpz2-96"),
        RouteRef("dpz8d-100"),
        RouteRef("dpz9-102"),
        RouteRef("dpz9j-116"),
        RouteRef("dpz9-129"),
        RouteRef("dpz2-165"),
        RouteRef("dpz8-501"),
        RouteRef("dpz83-504"),
        RouteRef("dpz8-505"),
        RouteRef("dpz8-506"),
        RouteRef("dpz83-509"),
        RouteRef("dpz83-510"),
        RouteRef("dpz83-511"),
        RouteRef("dpz82-512"),
        RouteRef("dpz2m-900"),
        RouteRef("dpz2-927"),
    ))

val TTC_FREQUENCY_COMMITMENT = FrequencyCommitment(
    spans = listOf(item),
    agency = TTC_ID)