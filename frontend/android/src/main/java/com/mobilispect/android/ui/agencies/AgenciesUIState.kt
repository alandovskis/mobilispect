package com.mobilispect.android.ui.agencies

sealed interface AgenciesUIState

object Loading : AgenciesUIState
object NoAgencyFound : AgenciesUIState
data class AgenciesFound(
    val agencies: Collection<AgencyUIState>
) : AgenciesUIState

data class AgencyUIState(val ref: String, val name: String)
