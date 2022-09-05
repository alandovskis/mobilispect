package com.mobilispect.common.data.frequency_commitment

import com.mobilispect.common.data.agency.AgencyRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestFrequencyCommitmentRepository: FrequencyCommitmentRepository {
    private val frequencyCommitmentsByAgency: MutableMap<AgencyRef, FrequencyCommitment> = mutableMapOf()

    override fun forAgency(agency: AgencyRef): Flow<FrequencyCommitment?> = flowOf(
        frequencyCommitmentsByAgency[agency]
    )

    fun insert(frequencyCommitment: FrequencyCommitment) {
         frequencyCommitmentsByAgency[frequencyCommitment.agency] = frequencyCommitment
    }
}
