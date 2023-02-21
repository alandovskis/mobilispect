package com.mobilispect.android.ui.agencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilispect.common.data.agency.AgencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AgenciesViewModel @Inject constructor(
    private val agencyRepository: AgencyRepository
) :
    ViewModel() {
    val uiState: Flow<AgenciesUIState> = agencyRepository.all()
        .mapLatest { agencies ->
            agencies.map { agency ->
                AgencyUIState(
                    id = agency.id,
                    name = agency.name
                )
            }
        }
        .mapLatest { agencies ->
            if (agencies.isNotEmpty()) {
                AgenciesFound(agencies)
            } else {
                NoAgencyFound
            }
        }

    fun sync() {
        viewModelScope.launch {
            agencyRepository.sync()
        }
    }
}
