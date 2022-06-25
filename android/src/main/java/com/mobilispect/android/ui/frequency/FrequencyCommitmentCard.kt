package com.mobilispect.android.ui.frequency

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilispect.android.R
import com.mobilispect.android.ui.theme.md_theme_dark_onPrimary
import com.mobilispect.android.ui.theme.md_theme_dark_primary

@Composable
fun FrequencyCommitmentCard(frequencyCommitmentViewModel: FrequencyCommitmentViewModel = viewModel()) {
    frequencyCommitmentViewModel.details()
    val uiState: FrequencyCommitmentUIState? by frequencyCommitmentViewModel.details.observeAsState()
    val items = uiState?.items ?: return
    Card {
        LazyColumn(content = {
            for (item in items) {
                item {
                    FrequencyCommitmentItemEntry(item)
                }
            }
        })
    }
}

@Composable
fun FrequencyCommitmentItemEntry(item: FrequencyCommitmentItemUIState) {
    Surface(color = md_theme_dark_primary, contentColor = md_theme_dark_onPrimary, shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)) {
        Column(modifier = Modifier.padding(start = 2.dp, end = 2.dp)) {
            DaysOfTheWeek(item.daysOfTheWeek)
            Direction(item.directions)
            Frequency(item.frequency)
            Routes(item.routes)
        }
    }
}

@Composable
private fun Routes(uiState: RoutesUIState) {
    Row {
        Text(text = stringResource(uiState.onRoutes), modifier = Modifier.align(CenterVertically))
    }

    for (route in uiState.routes) {
        Row {
            Emphasized(text = route)
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
        Text(text = stringResource(uiState.minutesOrLess), modifier = Modifier.align(CenterVertically))
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
                    stringResource(id = directionTime.from)}"
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
                text = stringResource(id = directionTime.to), modifier = Modifier.align(CenterVertically)
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
    Row {
        Text(
            text = stringResource(R.string.on_days), modifier = Modifier.align(CenterVertically)
        )
        Emphasized(stringResource(daysOfWeek))
    }
}

@Composable
private fun Emphasized(
    text: String, fontWeight: FontWeight = FontWeight.Bold, fontSize: TextUnit = 24.sp) {
    Text(text = text, fontWeight = fontWeight, fontSize = fontSize, modifier = Modifier.padding(4.dp, 4.dp))
}

@Preview(locale = "en", showBackground = true)
@Preview(locale = "fr", showBackground = true)
@Composable
fun PreviewFrequencyCommitmentCard() {
    FrequencyCommitmentCard()
}
