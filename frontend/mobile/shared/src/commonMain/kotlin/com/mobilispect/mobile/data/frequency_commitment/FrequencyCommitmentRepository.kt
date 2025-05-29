package com.mobilispect.mobile.data.frequency_commitment

import com.mobilispect.mobile.data.agency.STM_ID
import com.mobilispect.mobile.data.agency.TTC_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface FrequencyCommitmentRepository {
    fun forAgency(agency: String): Flow<FrequencyCommitment?>
}

class DefaultFrequencyCommitmentRepository @Inject constructor() : FrequencyCommitmentRepository {
    override fun forAgency(agency: String): Flow<FrequencyCommitment?> = flowOf(
        when (agency) {
            STM_ID -> STM_FREQUENCY_COMMITMENT
            TTC_ID -> TTC_FREQUENCY_COMMITMENT
            else -> null
        }
    )
}
