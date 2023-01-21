@file:Suppress("FunctionNaming", "PackageNaming")

package com.mobilispect.android.ui.frequency_violation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobilispect.android.R
import com.mobilispect.android.ui.Card
import com.mobilispect.android.ui.ScreenFrame
import com.mobilispect.common.data.route.RouteRef
import com.mobilispect.common.data.schedule.Direction
import com.mobilispect.common.domain.frequency_violation.NoDeparturesFound

@Composable
fun FrequencyViolationRoute(
    viewModel: FrequencyViolationViewModel = hiltViewModel(),
    routeRef: String?
) {
    RouteRef.fromString(routeRef)?.let {
        viewModel.findFrequencyViolationsAgainstScheduleForFirstStopAndDay(
            it
        )
        val uiState by viewModel.violations.collectAsState(
            initial = FrequencyViolationUIState(
                inbound = Result.success(emptyList()),
                outbound = Result.success(emptyList())
            )
        )
        FrequencyViolationScreen(it.routeNumber, uiState)
    }
}

@Composable
fun FrequencyViolationScreen(
    routeNumber: String,
    uiState: FrequencyViolationUIState,
) {
    ScreenFrame(
        stringResource(
            id = R.string.frequency_violations,
            routeNumber
        )
    ) { modifier ->
        Column(modifier = modifier) {
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
