package com.mobilispect.mobile.android.ui.frequency_violation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mobilispect.mobile.android.R
import com.mobilispect.mobile.android.ui.Card
import com.mobilispect.mobile.android.ui.ScreenFrame
import com.mobilispect.mobile.data.schedule.Direction
import com.mobilispect.mobile.domain.frequency_violation.NoDeparturesFound
import com.mobilispect.mobile.ui.agencies.AgenciesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FrequencyViolationRoute(
    routeRef: String?,
) {
    val viewModel: FrequencyViolationViewModel = koinViewModel()
    routeRef?.let {
        viewModel.findFrequencyViolationsAgainstScheduleForFirstStopAndDay(
            it
        )
        val uiState by viewModel.violations.collectAsState(
            initial = FrequencyViolationUIState(
                inbound = Result.success(emptyList()),
                outbound = Result.success(emptyList())
            )

        )
        FrequencyViolationScreen(it, uiState)
    }
}

@Composable
fun FrequencyViolationScreen(
    routeNumber: String,
    uiState: FrequencyViolationUIState,
    modifier: Modifier = Modifier
) {
    ScreenFrame(
        screenTitle = stringResource(
            id = R.string.frequency_violations,
            routeNumber
        ),
        modifier = modifier,
    ) {
        Column(modifier = Modifier) {
            FrequencyViolationCard(
                direction = Direction.Inbound,
                uiState.inbound
            )
            FrequencyViolationCard(
                direction = Direction.Outbound,
                uiState.outbound
            )
        }
    }
}

@Composable
private fun FrequencyViolationCard(
    direction: Direction,
    violations: Result<List<FrequencyViolationInstanceUIState>>
) {
    Card(title = "Direction $direction") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            violations.fold(
                onFailure = {
                    val message = when (it) {
                        NoDeparturesFound -> stringResource(id = R.string.no_departures_found)
                        else -> ""
                    }
                    Text(text = message)
                },
                onSuccess = {
                    LazyColumn {
                        for (violation in it) {
                            item {
                                Text("Between ${violation.start} and ${violation.end}")
                            }
                        }
                    }
                })
        }
    }
}
