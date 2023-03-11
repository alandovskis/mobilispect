package com.mobilispect.backend.batch

import com.mobilispect.backend.data.Agency

/**
 * The combination of agencies extracted and any paging parameters.
 */
data class AgencyResult(val agencies: Collection<Agency>, val after: Int)
