package com.mobilispect.backend.data.gtfs

import kotlinx.serialization.Serializable

@Serializable
class GTFSStop(val stop_id: String, val stop_name: String)
