package com.mobilispect.common.data.frequency_commitment

data class FrequencyCommitment(
    val spans: Collection<FrequencyCommitmentItem> = listOf(),
    val agency: String,
) {
    fun items(): Collection<FrequencyCommitmentItem> = spans
}