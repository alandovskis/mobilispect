package com.mobilispect.android.ui.agencies

import com.mobilispect.common.data.agency.AgencyRef

sealed interface AgenciesUIState

object Loading : AgenciesUIState
object NoAgencyFound : AgenciesUIState
data class AgenciesFound(
    val agencies: Collection<AgencyUIState>
) : AgenciesUIState

data class AgencyUIState(val ref: AgencyRef, val name: String)
