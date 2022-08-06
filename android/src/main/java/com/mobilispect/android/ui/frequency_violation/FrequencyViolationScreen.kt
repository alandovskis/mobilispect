package com.mobilispect.android.ui.frequency_violation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobilispect.android.R
import com.mobilispect.android.ui.Card
import com.mobilispect.android.ui.ScreenFrame
import com.mobilispect.data.frequency.Direction
import com.mobilispect.data.frequency.NoDeparturesFound
import com.mobilispect.data.routes.RouteRef

@Composable
fun FrequencyViolationRoute(routeRef: String?) {
    RouteRef.fromString(routeRef)?.let {
        FrequencyViolationScreen(it)
    }
}

@Composable
fun FrequencyViolationScreen(routeRef: RouteRef, frequencyViolationViewModel: FrequencyViolationViewModel = hiltViewModel()) {
    frequencyViolationViewModel.findFrequencyViolationsAgainstScheduleForFirstStopAndDay(routeRef)
    val violations by frequencyViolationViewModel.violations.observeAsState()

    ScreenFrame(stringResource(id = R.string.frequency_violations, routeRef.routeNumber)) {
        Column {
            if (violations != null) {
                FrequencyViolationCard(
                    direction = Direction.Inbound,
                    violations!!.inbound
                )
                FrequencyViolationCard(
                    direction = Direction.Outbound,
                    violations!!.outbound
                )
            }
        }
    }
}

@Composable
private fun FrequencyViolationCard(
    direction: Direction,
    violations: Result<List<FrequencyViolationInstanceUIState>>
) {
    Card(title = "Direction $direction") {
        Column(modifier = Modifier
                .fillMaxWidth()
        ) {
            if (violations.isFailure) {
                val message = when (violations.exceptionOrNull()!!) {
                    NoDeparturesFound -> stringResource(id = R.string.no_departures_found)
                    else -> ""
                }
                Text(text = message)
            } else {
                val data = violations.getOrNull()!!
                LazyColumn {
                    for (violation in data) {
                        item {
                            Text("Between ${violation.start} and ${violation.end}")
                        }
                    }
                }
            }
        }
    }
}
