package com.mobilispect.common.data.frequency_commitment

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestFrequencyCommitmentRepository : FrequencyCommitmentRepository {
    private val frequencyCommitmentsByAgency: MutableMap<String, FrequencyCommitment> =
        mutableMapOf()

    override fun forAgency(agency: String): Flow<FrequencyCommitment?> = flowOf(
        frequencyCommitmentsByAgency[agency]
    )

    fun insert(frequencyCommitment: FrequencyCommitment) {
        frequencyCommitmentsByAgency[frequencyCommitment.agency] = frequencyCommitment
    }
}
