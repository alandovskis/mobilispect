package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.schedule.ScheduledStop
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

const val TRANSIT_LAND_DEPARTURE_FULL_FIXTURE = """
{
    "stops": [
    {
        "departures": [],
        "feed_version": {
            "feed": {
                "id": 68,
                "onestop_id": "f-f25d-socitdetransportdemontral"
            },
            "fetched_at": "2023-03-21T14:00:49.488122Z",
            "id": 315990,
            "sha1": "f41195513247d422c278811c9fb4b55db40cf210"
        },
        "geometry": {
            "coordinates": [
                -73.603118,
                45.446466
            ],
            "type": "Point"
        },
        "onestop_id": "s-f25dt17bg5-stationangrignon",
        "parent": null,
        "platform_code": null,
        "stop_code": "10118",
        "stop_desc": "",
        "stop_id": "STATION_M118",
        "stop_name": "STATION ANGRIGNON",
        "stop_timezone": "",
        "stop_url": "",
        "tts_stop_name": null,
        "wheelchair_boarding": 1,
        "zone_id": ""
    },
    {
        "departures": [
        {
            "arrival": {
                "delay": null,
                "estimated": null,
                "estimated_utc": null,
                "scheduled": "05:30:00",
                "uncertainty": null
            },
            "arrival_time": "05:30:00",
            "continuous_drop_off": null,
            "continuous_pickup": null,
            "departure": {
                "delay": null,
                "estimated": null,
                "estimated_utc": null,
                "scheduled": "05:30:00",
                "uncertainty": null
            },
            "departure_time": "05:30:00",
            "drop_off_type": null,
            "interpolated": null,
            "pickup_type": null,
            "service_date": "2023-02-21",
            "shape_dist_traveled": 0,
            "stop_headsign": null,
            "stop_sequence": 1,
            "timepoint": null,
            "trip": {
                "bikes_allowed": 0,
                "block_id": "",
                "direction_id": 0,
                "frequencies": [],
                "id": 2134940224,
                "route": {
                    "agency": {
                        "agency_id": "STM",
                        "agency_name": "Société de transport de Montréal",
                        "id": 376089,
                        "onestop_id": "o-f25d-socitdetransportdemontral"
                    },
                    "continuous_drop_off": null,
                    "continuous_pickup": null,
                    "id": 21663718,
                    "onestop_id": "r-f25d-1",
                    "route_color": "00B300",
                    "route_desc": "",
                    "route_id": "1",
                    "route_long_name": "Verte",
                    "route_short_name": "1",
                    "route_text_color": "000000",
                    "route_type": 1,
                    "route_url": "http://www.stm.info/fr/infos/reseaux/metro/verte"
                },
                "shape": {
                    "generated": false,
                    "id": 102780752,
                    "shape_id": "11071"
                },
                "stop_pattern_id": 613,
                "timestamp": null,
                "trip_headsign": "STATION HONORÉ-BEAUGRAND",
                "trip_id": "263076528",
                "trip_short_name": "",
                "wheelchair_accessible": 1
            }
        },
        {
            "arrival": {
                "delay": null,
                "estimated": null,
                "estimated_utc": null,
                "scheduled": "25:40:00",
                "uncertainty": null
            },
            "arrival_time": "25:40:00",
            "continuous_drop_off": null,
            "continuous_pickup": null,
            "departure": {
                "delay": null,
                "estimated": null,
                "estimated_utc": null,
                "scheduled": "25:40:00",
                "uncertainty": null
            },
            "departure_time": "25:40:00",
            "drop_off_type": null,
            "interpolated": null,
            "pickup_type": null,
            "service_date": "2023-02-21",
            "shape_dist_traveled": 0,
            "stop_headsign": null,
            "stop_sequence": 1,
            "timepoint": null,
            "trip": {
                "bikes_allowed": 0,
                "block_id": "",
                "direction_id": 0,
                "frequencies": [],
                "id": 2134940258,
                "route": {
                    "agency": {
                        "agency_id": "STM",
                        "agency_name": "Société de transport de Montréal",
                        "id": 376089,
                        "onestop_id": "o-f25d-socitdetransportdemontral"
                    },
                    "continuous_drop_off": null,
                    "continuous_pickup": null,
                    "id": 21663718,
                    "onestop_id": "r-f25d-1",
                    "route_color": "00B300",
                    "route_desc": "",
                    "route_id": "1",
                    "route_long_name": "Verte",
                    "route_short_name": "1",
                    "route_text_color": "000000",
                    "route_type": 1,
                    "route_url": "http://www.stm.info/fr/infos/reseaux/metro/verte"
                },
                "shape": {
                    "generated": false,
                    "id": 102780752,
                    "shape_id": "11071"
                },
                "stop_pattern_id": 613,
                "timestamp": null,
                "trip_headsign": "STATION HONORÉ-BEAUGRAND",
                "trip_id": "263076632",
                "trip_short_name": "",
                "wheelchair_accessible": 1
            }
        }
        ],
        "feed_version": {
            "feed": {
                "id": 68,
                "onestop_id": "f-f25d-socitdetransportdemontral"
            },
            "fetched_at": "2023-03-21T14:00:49.488122Z",
            "id": 315990,
            "sha1": "f41195513247d422c278811c9fb4b55db40cf210"
        },
        "geometry": {
            "coordinates": [
                -73.603118,
                45.446466
            ],
            "type": "Point"
        },
        "id": 462592246,
        "onestop_id": "s-f25dt17bg5-stationangrignon",
        "parent": {
            "geometry": {
                "coordinates": [
                    -73.603118,
                    45.446466
                ],
                "type": "Point"
            },
            "id": 462592178,
            "onestop_id": "s-f25dt17bg5-stationangrignon",
            "platform_code": null,
            "stop_code": "10118",
            "stop_desc": "",
            "stop_id": "STATION_M118",
            "stop_name": "STATION ANGRIGNON",
            "stop_timezone": "",
            "stop_url": "",
            "tts_stop_name": null,
            "wheelchair_boarding": 1,
            "zone_id": ""
        },
        "platform_code": null,
        "stop_code": "10118",
        "stop_desc": "",
        "stop_id": "43",
        "stop_name": "Station Angrignon",
        "stop_timezone": "",
        "stop_url": "http://www.stm.info/fr/infos/reseaux/metro/angrignon",
        "tts_stop_name": null,
        "wheelchair_boarding": 1,
        "zone_id": ""
    }
    ]
}
"""

const val TRANSIT_LAND_DEPARTURE_MINIMAL_FIXTURE = """
{
    "stops": [
    {
        "departures": [],
        "onestop_id": "s-f25dt17bg5-stationangrignon"
    },
    {
        "departures": [
        {
            "arrival": {
                "scheduled": "05:30:00"
            },
            "departure": {
                "scheduled": "05:30:00"
            },
            "service_date": "2023-02-21",
            "trip": {
                "route": {
                    "onestop_id": "r-f25d-1"
                },
                "trip_headsign": "STATION HONORÉ-BEAUGRAND"
            }
        },
        {
            "arrival": {
                "scheduled": "25:40:00"
            },
            "departure": {
                "scheduled": "25:40:00"
            },
            "service_date": "2023-02-21",
            "trip": {
                "route": {
                    "onestop_id": "r-f25d-1"
                },
                "trip_headsign": "STATION HONORÉ-BEAUGRAND"
            }
        }
        ],
        "onestop_id": "s-f25dt17bg5-stationangrignon"
    }
    ]
}
"""

val TRANSIT_LAND_DEPARTURE_1 = ScheduledStop(
    routeID = "r-f25d-1",
    stopID = "s-f25dt17bg5-stationangrignon",
    departsAt = LocalDateTime.of(LocalDate.of(2023, 3, 22), LocalTime.of(5, 30)),
    arrivesAt = LocalDateTime.of(LocalDate.of(2023, 3, 22), LocalTime.of(5, 30)),
    direction = "STATION HONORÉ-BEAUGRAND"
)
val TRANSIT_LAND_DEPARTURE_2 = ScheduledStop(
    routeID = "r-f25d-1",
    stopID = "s-f25dt17bg5-stationangrignon",
    departsAt = LocalDateTime.of(LocalDate.of(2023, 3, 23), LocalTime.of(1, 40)),
    arrivesAt = LocalDateTime.of(LocalDate.of(2023, 3, 23), LocalTime.of(1, 40)),
    direction = "STATION HONORÉ-BEAUGRAND"
)
