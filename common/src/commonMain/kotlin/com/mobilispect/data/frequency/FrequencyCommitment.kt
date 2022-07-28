package com.mobilispect.data.frequency

import com.mobilispect.data.agency.AgencyRef

data class FrequencyCommitment (
    val spans: Collection<FrequencyCommitmentItem> = listOf(),
    val agency: AgencyRef,
) {
    fun items(): Collection<FrequencyCommitmentItem> = spans
}