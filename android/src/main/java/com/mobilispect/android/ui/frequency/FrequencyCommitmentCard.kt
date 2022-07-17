package com.mobilispect.android.ui.frequency

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobilispect.android.R
import com.mobilispect.android.ui.Card
import com.mobilispect.android.ui.ScreenFrame
import com.mobilispect.android.ui.theme.MobilispectTheme
import com.mobilispect.common.data.routes.RouteRef
import java.time.LocalTime

@Composable
fun FrequencyCommitmentRoute(
    viewModel: FrequencyCommitmentViewModel = hiltViewModel(),
    navigateToViolation: (RouteRef) -> Unit
) {
    val uiState: FrequencyCommitmentUIState? by viewModel.details.collectAsState(
        initial = null
    )

    FrequencyCommitmentScreen(uiState, navigateToViolation)
}

@Composable
fun FrequencyCommitmentScreen(uiState: FrequencyCommitmentUIState?, navigateToViolation: (RouteRef) -> Unit) {
    ScreenFrame(screenTitle = stringResource(id = R.string.frequency_commitment)) {
        FrequencyCommitmentEntryCards(uiState = uiState, navigateToViolation = navigateToViolation)
    }
}

@Composable
fun FrequencyCommitmentEntryCards(uiState: FrequencyCommitmentUIState?, navigateToViolation: (RouteRef) -> Unit) {
    val items = uiState?.items ?: return
    Column {
        for (item in items) {
            FrequencyCommitmentCard(item, navigateToViolation)
        }
    }
}

@Composable
fun FrequencyCommitmentCard(
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
private fun Routes(uiState: RoutesUIState, onRoutePressed: (RouteRef) -> Unit) {
    Row {
        Text(text = stringResource(uiState.onRoutes), modifier = Modifier.align(CenterVertically))
    }

    for (route in uiState.routes) {
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
private fun Frequency(uiState: FrequencyCommitmentFrequencyUIState) {
    Row {
        Text(text = stringResource(uiState.every), modifier = Modifier.align(CenterVertically))
        Emphasized(
            text = uiState.frequency.toString(),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(uiState.minutesOrLess),
            modifier = Modifier.align(CenterVertically)
        )
    }
}

@Composable
private fun Direction(directions: Collection<FrequencyCommitmentDirectionUIState>) {
    for (directionTime in directions) {
        Row {
            val timeFormatter = directionTime.timeFormatter.withLocale(
                LocalContext.current.resources.configuration.locales[0]
            )

            val direction = if (directionTime.direction != null) {
                "${stringResource(id = directionTime.direction)} ${
                    stringResource(id = directionTime.from)
                }"
            } else {
                stringResource(id = directionTime.from)
            }

            Text(
                text = direction,
                modifier = Modifier.align(CenterVertically)
            )
            Emphasized(
                text = directionTime.startTime.format(timeFormatter),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = directionTime.to),
                modifier = Modifier.align(CenterVertically)
            )
            Emphasized(
                text = directionTime.endTime.format(timeFormatter),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DaysOfTheWeek(daysOfWeek: Int) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.on_days), modifier = Modifier.align(CenterVertically),
            fontSize = MaterialTheme.typography.body1.fontSize,
            fontWeight = MaterialTheme.typography.body1.fontWeight,
            fontStyle = MaterialTheme.typography.body1.fontStyle
        )
        Emphasized(stringResource(daysOfWeek))
    }
}

@Composable
private fun Emphasized(
    text: String, fontWeight: FontWeight = FontWeight.Bold, fontSize: TextUnit = 24.sp
) {
    Text(
        text = text,
        fontWeight = fontWeight,
        fontSize = fontSize,
        modifier = Modifier.padding(4.dp, 4.dp),
    )
}

@Preview(name = "English", locale = "en", showBackground = true)
@Preview(name = "French", locale = "fr", showBackground = true)
@Composable
fun PreviewFrequencyCommitmentCard() {
    MobilispectTheme {
        val uiState = FrequencyCommitmentItemUIState(
            daysOfTheWeek = R.string.weekdays,
            directions = listOf(
                FrequencyCommitmentDirectionUIState(
                    direction = R.string.inbound,
                    startTime = LocalTime.of(6, 0),
                    endTime = LocalTime.of(21, 0),
                )
            ),
            frequency = FrequencyCommitmentFrequencyUIState(
                frequency = 10,
            ),
            routes = RoutesUIState(
                onRoutes = R.string.on_routes,
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
        )

        FrequencyCommitmentCard(uiState, navigateToViolation = {})
    }
}
