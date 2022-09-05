package com.mobilispect.common.data.frequency_commitment

import com.mobilispect.common.data.agency.AgencyRef

data class FrequencyCommitment (
    val spans: Collection<FrequencyCommitmentItem> = listOf(),
    val agency: AgencyRef,
) {
    fun items(): Collection<FrequencyCommitmentItem> = spans
}