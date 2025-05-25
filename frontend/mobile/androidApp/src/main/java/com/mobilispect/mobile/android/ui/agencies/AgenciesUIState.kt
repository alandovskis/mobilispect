package com.mobilispect.mobile.android.ui.agencies

sealed interface AgenciesUIState

object Loading : AgenciesUIState
object NoAgencyFound : AgenciesUIState
data class AgenciesFound(
    val agencies: Collection<AgencyUIState>
) : AgenciesUIState

data class AgencyUIState(val id: String, val name: String)
