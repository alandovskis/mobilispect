package com.mobilispect.backend.data.agency

import com.mobilispect.backend.data.Agency

/**
 * The combination of agencies extracted and any paging parameters.
 */
data class AgencyResult(val agencies: Collection<Agency>, val after: Int)
