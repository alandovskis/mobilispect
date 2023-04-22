package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.stop.StopResultItem

const val TRANSIT_LAND_STOPS_SUCCESS_FIXTURE = """
{
    "meta": {
        "after": 439365585,
        "next": "https://api.transit.land/api/v2/rest/stops.json?after=439365585&limit=2&served_by_onestop_ids=o-f25d-socitdetransportdemontral"
    },
    "stops": [
        {
            "feed_version": {
                "feed": {
                    "id": 68,
                    "onestop_id": "f-f25d-socitdetransportdemontral"
                },
                "fetched_at": "2023-02-17T17:00:43.739861Z",
                "id": 309368,
                "sha1": "d503db302c07cdc0f4d6ff23bdfbb899e73b9f68"
            },
            "geometry": {
                "coordinates": [
                    -73.603118,
                    45.446466
                ],
                "type": "Point"
            },
            "id": 439365583,
            "level": null,
            "location_type": 0,
            "onestop_id": "s-f25dt17bg5-stationangrignon",
            "parent": {
                "geometry": {
                    "coordinates": [
                        -73.603118,
                        45.446466
                    ],
                    "type": "Point"
                },
                "id": 439365515,
                "stop_id": "STATION_M118",
                "stop_name": "STATION ANGRIGNON"
            },
            "platform_code": null,
            "stop_code": "10118",
            "stop_desc": "",
            "stop_id": "43",
            "stop_name": "Station Angrignon",
            "stop_timezone": "",
            "stop_url": "http://www.stm.info/fr/infos/reseaux/metro/angrignon",
            "tts_stop_name": null,
            "wheelchair_boarding": 2,
            "zone_id": ""
        },
        {
            "feed_version": {
                "feed": {
                    "id": 68,
                    "onestop_id": "f-f25d-socitdetransportdemontral"
                },
                "fetched_at": "2023-02-17T17:00:43.739861Z",
                "id": 309368,
                "sha1": "d503db302c07cdc0f4d6ff23bdfbb899e73b9f68"
            },
            "geometry": {
                "coordinates": [
                    -73.593242,
                    45.451158
                ],
                "type": "Point"
            },
            "id": 439365585,
            "level": null,
            "location_type": 0,
            "onestop_id": "s-f25dt65h1j-stationmonk",
            "parent": {
                "geometry": {
                    "coordinates": [
                        -73.593242,
                        45.451158
                    ],
                    "type": "Point"
                },
                "id": 439365516,
                "stop_id": "STATION_M120",
                "stop_name": "STATION MONK"
            },
            "platform_code": null,
            "stop_code": "10120",
            "stop_desc": "",
            "stop_id": "42",
            "stop_name": "Station Monk",
            "stop_timezone": "",
            "stop_url": "http://www.stm.info/fr/infos/reseaux/metro/monk",
            "tts_stop_name": null,
            "wheelchair_boarding": 2,
            "zone_id": ""
        }
    ]
}
"""

val TRANSIT_LAND_STOP_1 = StopResultItem(
    id = "s-f25dt17bg5-stationangrignon",
    stopID = "43",
)
val TRANSIT_LAND_STOP_2 = StopResultItem(
    id = "s-f25dt65h1j-stationmonk",
    stopID = "42",
)
