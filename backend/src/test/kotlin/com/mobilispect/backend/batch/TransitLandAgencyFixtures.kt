package com.mobilispect.backend.batch

const val TRANSIT_LAND_AGENCIES_SUCCESS: String = """
{
  "agencies": [
    {
      "agency_email": "",
      "agency_fare_url": "",
      "agency_id": "MTM",
      "agency_lang": "it",
      "agency_name": "MTM Mobilita' e Trasporti Molfetta",
      "agency_phone": "390803387574",
      "agency_timezone": "Europe/Rome",
      "agency_url": "http://www.mtmmolfetta.it",
      "feed_version": {
        "feed": {
          "id": 311,
          "onestop_id": "f-sr7f3-mtmmobilitaetrasportimolfetta"
        },
        "fetched_at": "2016-07-14T15:22:13.475626Z",
        "id": 1353,
        "sha1": "d043eb31ed57955e134917efbcd8912ccacd74d6"
      },
      "geometry": {
        "coordinates": [
          [
            [
              16.5743239569561,
              41.1724305772135
            ],
            [
              16.5415012977306,
              41.2141327810569
            ],
            [
              16.5496091481395,
              41.2143614292278
            ],
            [
              16.5969338709327,
              41.2051727205418
            ],
            [
              16.5995220404677,
              41.2046510353212
            ],
            [
              16.6016867719422,
              41.2036571246959
            ],
            [
              16.613556,
              41.197619
            ],
            [
              16.6119517345105,
              41.1964155853783
            ],
            [
              16.5979276113072,
              41.1861577703302
            ],
            [
              16.5743239569561,
              41.1724305772135
            ]
          ]
        ],
        "type": "Polygon"
      },
      "id": 4117,
      "onestop_id": "o-sr7f3-mtmmobilitaetrasportimolfetta",
      "operator": {
        "feeds": [
          {
            "id": 311,
            "name": null,
            "onestop_id": "f-sr7f3-mtmmobilitaetrasportimolfetta",
            "spec": "GTFS"
          }
        ],
        "name": "Mobilità e Trasporti Molfetta",
        "onestop_id": "o-sr7f3-mtmmobilitaetrasportimolfetta",
        "short_name": "MTM",
        "tags": null
      },
      "places": [
        {
          "adm0_name": "Italy",
          "adm1_name": "Bari",
          "city_name": null
        }
      ]
    },
    {
      "agency_email": "",
      "agency_fare_url": "",
      "agency_id": "EPS",
      "agency_lang": "en",
      "agency_name": "Town of Estes Park",
      "agency_phone": "(970) 577-7477",
      "agency_timezone": "America/Denver",
      "agency_url": "http://www.estes.org/shuttles",
      "feed_version": {
        "feed": {
          "id": 333,
          "onestop_id": "f-9xhvw-townofestespark"
        },
        "fetched_at": "2016-07-15T20:45:14.774386Z",
        "id": 1374,
        "sha1": "913b07b945e2b2afbc5d37d7bcd883918933c9ce"
      },
      "geometry": {
        "coordinates": [
          [
            [
              -105.531135,
              40.338083
            ],
            [
              -105.574274,
              40.340897
            ],
            [
              -105.586316,
              40.401946
            ],
            [
              -105.490773,
              40.39125
            ],
            [
              -105.482392,
              40.376607
            ],
            [
              -105.492599,
              40.36786
            ],
            [
              -105.531135,
              40.338083
            ]
          ]
        ],
        "type": "Polygon"
      },
      "id": 4116,
      "onestop_id": "o-9xhvw-townofestespark",
      "operator": {
        "feeds": [
          {
            "id": 333,
            "name": null,
            "onestop_id": "f-9xhvw-townofestespark",
            "spec": "GTFS"
          }
        ],
        "name": "Town of Estes Park",
        "onestop_id": "o-9xhvw-townofestespark",
        "short_name": "Estes Park Free Shuttles",
        "tags": {
          "twitter_general": "TownofEstesPark"
        }
      },
      "places": [
        {
          "adm0_name": "United States of America",
          "adm1_name": "Colorado",
          "city_name": null
        }
      ]
    }
  ],
  "meta": {
    "after": 3973,
    "next": "https://api.transit.land/api/v2/rest/agencies.json?after=3973&limit=100"
  }
}
"""

val TRANSIT_LAND_SUCCESS_AGENCY_1 = TransitLandAgency(
    email = "",
    fareURL = "",
    agencyID = "MTM",
    language = "it",
    name = "MTM Mobilita' e Trasporti Molfetta",
    phone = "390803387574",
    timezone = "Europe/Rome",
    url = "http://www.mtmmolfetta.it",
    feed = FeedVersion(
        feed = Feed(id = 311, oneStopID = "f-sr7f3-mtmmobilitaetrasportimolfetta"),
        fetchedAt = "2016-07-14T15:22:13.475626Z",
        id = 1353,
        version = "d043eb31ed57955e134917efbcd8912ccacd74d6"
    ),
    geometry = Geometry(
        coordinates = arrayOf(
            arrayOf(
                arrayOf(16.5743239569561, 41.1724305772135),
                arrayOf(16.5415012977306, 41.2141327810569),
                arrayOf(16.5496091481395, 41.2143614292278),
                arrayOf(16.5969338709327, 41.2051727205418),
                arrayOf(16.5995220404677, 41.2046510353212),
                arrayOf(16.6016867719422, 41.2036571246959),
                arrayOf(16.613556, 41.197619),
                arrayOf(16.6119517345105, 41.1964155853783),
                arrayOf(16.5979276113072, 41.1861577703302),
                arrayOf(16.5743239569561, 41.1724305772135)
            )
        ), type = "Polygon"
    ),
    id = 4117,
    onestopID = "o-sr7f3-mtmmobilitaetrasportimolfetta",
    operator = Operator(
        feeds = arrayOf(
            OperatorFeed(
                id = 311, name = null, onestopID = "f-sr7f3-mtmmobilitaetrasportimolfetta", spec = "GTFS"
            )
        ),
        name = "Mobilità e Trasporti Molfetta",
        oneStopID = "o-sr7f3-mtmmobilitaetrasportimolfetta",
        shortName = "MTM",
        tags = null
    ),
    places = arrayOf(Place(country = "Italy", provinceState = "Bari", city = null))
)

val TRANSIT_LAND_SUCCESS_AGENCY_2 = TransitLandAgency(
    email = "",
    fareURL = "",
    agencyID = "EPS",
    language = "en",
    name = "Town of Estes Park",
    phone = "(970) 577-7477",
    timezone = "America/Denver",
    url = "http://www.estes.org/shuttles",
    feed = FeedVersion(
        feed = Feed(id = 333, oneStopID = "f-9xhvw-townofestespark"),
        fetchedAt = "2016-07-15T20:45:14.774386Z",
        id = 1374,
        version = "913b07b945e2b2afbc5d37d7bcd883918933c9ce"
    ),
    geometry = Geometry(
        coordinates = arrayOf(
            arrayOf(
                arrayOf(-105.531135, 40.338083),
                arrayOf(-105.574274, 40.340897),
                arrayOf(-105.586316, 40.401946),
                arrayOf(-105.490773, 40.39125),
                arrayOf(-105.482392, 40.376607),
                arrayOf(-105.492599, 40.36786),
                arrayOf(-105.531135, 40.338083)
            ),
        ),
        type = "Polygon"
    ),
    id = 4116,
    onestopID = "o-9xhvw-townofestespark",
    operator = Operator(
        feeds = arrayOf(
            OperatorFeed(
                id = 333,
                name = null,
                onestopID = "f-9xhvw-townofestespark",
                spec = "GTFS"
            )
        ),
        name = "Town of Estes Park",
        oneStopID = "o-9xhvw-townofestespark",
        shortName = "Estes Park Free Shuttles",
        tags = Tags(twitter = "TownofEstesPark")
    ),
    places = arrayOf(
        Place(country = "United States of America", provinceState = "Colorado", city = null)
    )
)
