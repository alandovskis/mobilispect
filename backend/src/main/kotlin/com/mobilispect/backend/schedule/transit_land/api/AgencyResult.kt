package com.mobilispect.backend.schedule.transit_land.api

/**
 * The combination of agencies extracted and any paging parameters.
 */
data class AgencyResult(val agencies: Collection<AgencyResultItem>, val after: Int)
