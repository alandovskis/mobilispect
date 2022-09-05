package com.mobilispect.common.data.frequency_commitment

import com.mobilispect.common.data.agency.AgencyRef
import com.mobilispect.common.data.agency.STM_ID
import com.mobilispect.common.data.agency.TTC_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface FrequencyCommitmentRepository {
    fun forAgency(agency: AgencyRef): Flow<FrequencyCommitment?>
}

class DefaultFrequencyCommitmentRepository @Inject constructor() : FrequencyCommitmentRepository {
    override fun forAgency(agency: AgencyRef): Flow<FrequencyCommitment?> = flowOf(
        when (agency) {
            STM_ID -> STM_FREQUENCY_COMMITMENT
            TTC_ID -> TTC_FREQUENCY_COMMITMENT
            else -> null
        }
    )
}
