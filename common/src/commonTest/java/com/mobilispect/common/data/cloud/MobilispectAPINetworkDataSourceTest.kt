package com.mobilispect.common.data.cloud

import kotlin.test.Test

class MobilispectAPINetworkDataSourceTest {

    @Test
    fun agencies() {
        val mockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel(
                    """
                        {
                          "_embedded" : {
                            "agencies" : [ {
                              "name" : "A",
                              "_links" : {
                                "self" : {
                                  "href" : "http://localhost:61343/agencies/o-abcd-a"
                                },
                                "agency" : {
                                  "href" : "http://localhost:61343/agencies/o-abcd-a"
                                }
                              }
                            }, {
                              "name" : "B",
                              "_links" : {
                                "self" : {
                                  "href" : "http://localhost:61343/agencies/o-abcd-b"
                                },
                                "agency" : {
                                  "href" : "http://localhost:61343/agencies/o-abcd-b"
                                }
                              }
                            } ]
                          },
                          "_links" : {
                            "self" : {
                              "href" : "http://localhost:61343/agencies"
                            },
                            "profile" : {
                              "href" : "http://localhost:61343/profile/agencies"
                            }
                          },
                          "page" : {
                            "size" : 20,
                            "totalElements" : 2,
                            "totalPages" : 1,
                            "number" : 0
                          }
                        }
                    """
                        .trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val networkDataSource = MobilispectAPINetworkDataSource(mockEngine)

        val agencies = networkDataSource.agencies()

        fail()
    }
}