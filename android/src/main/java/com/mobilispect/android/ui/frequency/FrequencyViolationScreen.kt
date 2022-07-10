package com.mobilispect.android.ui.frequency

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
import com.mobilispect.android.ui.Item
import com.mobilispect.android.ui.ScreenFrame
import com.mobilispect.common.data.frequency.Direction
import com.mobilispect.common.data.routes.RouteRef

@Composable
fun FrequencyViolationScreen(frequencyViolationViewModel: FrequencyViolationViewModel = hiltViewModel()) {
    val routeRef = RouteRef(
        geohash = "f25em",
        routeNumber = "141"
    )
    frequencyViolationViewModel.findFrequencyViolationsAgainstScheduleForFirstStopAndDay(routeRef)
    val violations by frequencyViolationViewModel.violations.observeAsState()

    ScreenFrame(stringResource(id = R.string.frequency_violations, routeRef.routeNumber)) {
        if (violations != null) {
            FrequencyCommitmentRouteCard(
                direction = Direction.Inbound,
                violations!!.directions[Direction.Inbound]
            )
            FrequencyCommitmentRouteCard(
                direction = Direction.Outbound,
                violations!!.directions[Direction.Outbound]
            )
        }
    }
}

@Composable
private fun FrequencyCommitmentRouteCard(
    direction: Direction,
    violations: Result<List<FrequencyViolationInstanceUIState>>?
) {
    val data = violations?.getOrNull().orEmpty()
    Card(title = "Direction $direction") {
        Column(modifier = Modifier
                .fillMaxWidth()
        ) {
            LazyColumn {
                for (violation in data) {
                    item {
                        Item {
                            Text("Between ${violation.start} and ${violation.end}")
                        }
                    }
                }
            }
        }
    }
}
