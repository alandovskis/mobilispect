@file:Suppress("TooManyFunctions", "FunctionNaming")

package com.mobilispect.android.ui.frequency_commitment

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobilispect.android.R
import com.mobilispect.android.ui.Card
import com.mobilispect.android.ui.ScreenFrame
import com.mobilispect.android.ui.theme.MobilispectTheme
import com.mobilispect.common.data.agency.STM_ID
import com.mobilispect.common.data.route.RouteRef
import com.mobilispect.common.data.schedule.Direction.Inbound
import com.mobilispect.common.data.schedule.Direction.Outbound
import com.mobilispect.common.data.time.WEEKDAYS
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun FrequencyCommitmentRoute(
    viewModel: FrequencyCommitmentViewModel = hiltViewModel(),
    navigateToViolation: (RouteRef) -> Unit
) {
    val uiState by viewModel.uiState(STM_ID).collectAsState(
        initial = Loading
    )

    FrequencyCommitmentScreen(uiState, navigateToViolation)
}

@Composable
fun FrequencyCommitmentScreen(
    uiState: FrequencyCommitmentUIState,
    navigateToViolation: (RouteRef) -> Unit
) {
    ScreenFrame(screenTitle = stringResource(id = R.string.frequency_commitment)) { modifier ->
        FrequencyCommitmentEntryCards(
            uiState = uiState, navigateToViolation = navigateToViolation,
            modifier = modifier
        )
    }
}

@Composable
fun FrequencyCommitmentEntryCards(
    uiState: FrequencyCommitmentUIState,
    navigateToViolation: (RouteRef) -> Unit,
    modifier: Modifier
) {
    when (uiState) {
        Loading -> LoadingCard()
        NoCommitmentFound -> NotFoundCard()
        is CommitmentFound -> {
            val items = uiState.items
            Column(modifier = modifier) {
                for (item in items) {
                    FrequencyCommitmentCard(item, navigateToViolation)
                }
            }
        }
    }
}

@Composable
private fun LoadingCard() {
    CircularProgressIndicator(
        modifier = Modifier.size(100.dp),
        color = MaterialTheme.colors.primary
    )
}

@Composable
private fun NotFoundCard() {
    Card {
        Text(stringResource(id = R.string.no_frequency_commitment_was_found))
    }
}

@Composable
private fun FrequencyCommitmentCard(
    item: FrequencyCommitmentItemUIState,
    navigateToViolation: (RouteRef) -> Unit
) {
    Card {
        DaysOfTheWeek(item.daysOfTheWeek)
        Direction(item.directions)
        Frequency(item.frequency)
        Routes(item.routes, navigateToViolation)
    }
}

@Composable
private fun Routes(routes: Collection<RouteUIState>, onRoutePressed: (RouteRef) -> Unit) {
    Text(text = stringResource(R.string.on_routes))

    for (route in routes) {
        OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary
        ),
            border = BorderStroke(1.dp, MaterialTheme.colors.onSecondary),
            onClick = { onRoutePressed(route.routeRef) }) {
            Text(
                text = route.route,
                style = MaterialTheme.typography.button
            )
        }
    }
}

@Composable
private fun Frequency(frequency: Long) {
    Text(
        text = stringResource(id = R.string.every_n_minutes_or_less, frequency),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun Direction(directions: Collection<FrequencyCommitmentDirectionUIState>) {
    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        .withLocale(LocalContext.current.resources.configuration.locales[0])

    if (directions.isNotEmpty() && directions.first().isBothDirections) {
        val direction = directions.first()
        DirectionTime(direction, timeFormatter, R.string.from_start_to_end)
    } else {
        for (directionTime in directions) {
            val direction = directionTime.direction ?: continue
            val directionText = when (direction) {
                Inbound -> R.string.inbound_from_start_to_end
                Outbound -> R.string.outbound_from_start_to_end
            }
            DirectionTime(directionTime, timeFormatter, directionText)
        }
    }
}

@Composable
private fun DirectionTime(
    direction: FrequencyCommitmentDirectionUIState,
    timeFormatter: DateTimeFormatter?,
    @StringRes stringResource: Int
) {
    val directionText = stringResource(
        id = stringResource,
        direction.startTime.format(timeFormatter),
        direction.endTime.format(timeFormatter),
    )
    Text(text = directionText, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun DaysOfTheWeek(daysOfWeek: Collection<DayOfWeek>) {
    val days = if (daysOfWeek == WEEKDAYS) {
        stringResource(id = R.string.on_weekdays)
    } else {
        ""
    }

    Text(
        text = days,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(name = "Loading [Light]", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Loading [Dark]", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewLoadingCard() {
    MobilispectTheme {
        LoadingCard()
    }
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewFrequencyCommitmentCard() {
    MobilispectTheme {
        val uiState = FrequencyCommitmentItemUIState(
            daysOfTheWeek = WEEKDAYS,
            directions = listOf(
                FrequencyCommitmentDirectionUIState(
                    direction = Inbound,
                    startTime = LocalTime.of(6, 0),
                    endTime = LocalTime.of(21, 0),
                )
            ),
            frequency = 10,
            routes = listOf(
                RouteUIState(
                    route = "18: Beaubien",
                    routeRef = RouteRef(geohash = "f25ej", routeNumber = "18"),
                ),
                RouteUIState(
                    route = "24: Sherbrooke",
                    routeRef = RouteRef(geohash = "f25dv", routeNumber = "24")
                )
            )
        )

        FrequencyCommitmentCard(uiState, navigateToViolation = {})
    }
}
