package com.mobilispect.android.ui.frequency

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobilispect.android.R
import com.mobilispect.android.ui.theme.md_theme_dark_onPrimary
import com.mobilispect.android.ui.theme.md_theme_dark_primary
import com.mobilispect.common.data.frequency.Direction
import com.mobilispect.common.data.frequency.FrequencyCommitment
import com.mobilispect.common.data.frequency.FrequencyCommitmentItem
import com.mobilispect.common.data.frequency.STM_FREQUENCY_COMMITMENT
import com.mobilispect.common.data.time.WEEKDAYS
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun FrequencyCommitmentCard(frequencyCommitment: FrequencyCommitment) {
    Card {
        LazyColumn(content = {
            for (item in frequencyCommitment.items()) {
                item {
                    FrequencyCommitmentItemEntry(item)
                }
            }
        })
    }
}

@Composable
fun FrequencyCommitmentItemEntry(item: FrequencyCommitmentItem) {
    Surface(color = md_theme_dark_primary, contentColor = md_theme_dark_onPrimary, shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)) {
        Column(modifier = Modifier.padding(start = 2.dp, end = 2.dp)) {
            DaysOfTheWeek(item)
            Direction(item)
            Frequency(item)
            Routes(item)
        }
    }
}

@Composable
private fun Routes(item: FrequencyCommitmentItem) {
    Row {
        Text(text = stringResource(R.string.on_routes), modifier = Modifier.align(CenterVertically))
    }

    for (route in item.routes) {
        Row {
            Emphasized(text = "- $route")
        }
    }
}

@Composable
private fun Frequency(item: FrequencyCommitmentItem) {
    Row {
        Text(text = stringResource(R.string.every), modifier = Modifier.align(CenterVertically))
        Emphasized(
            text = item.frequency.toMinutes().toString(),
            fontWeight = FontWeight.Bold
        )
        Text(text = stringResource(R.string.minutes_or_less), modifier = Modifier.align(CenterVertically))
    }
}

@Composable
private fun Direction(item: FrequencyCommitmentItem) {
    val first = item.directions.first()
    val from = stringResource(id = R.string.from)
    val to = stringResource(R.string.to)
    val ofLocalizedTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        .withLocale(LocalContext.current.resources.configuration.locales[0])

    if (item.directions.all { it.start == first.start && it.end == first.end }) {
        Row {
            Text(
                text = from,
                modifier = Modifier.align(CenterVertically)
            )
            Emphasized(
                text = first.start.format(
                    ofLocalizedTime
                ),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = to, modifier = Modifier.align(CenterVertically)
            )
            Emphasized(
                text = first.end.format(ofLocalizedTime),
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        for (directionTime in item.directions) {
            Row {
                val direction = when (directionTime.direction) {
                    Direction.Inbound -> stringResource(R.string.inbound)
                    Direction.Outbound -> stringResource(R.string.outbound)
                }
                Text(
                    text = "$direction $from",
                    modifier = Modifier.align(CenterVertically)
                )
                Emphasized(
                    text = directionTime.start.format(ofLocalizedTime),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = to, modifier = Modifier.align(CenterVertically)
                )
                Emphasized(
                    text = directionTime.end.format(ofLocalizedTime),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DaysOfTheWeek(item: FrequencyCommitmentItem) {
    Row {
        Text(
            text = stringResource(R.string.on_days), modifier = Modifier.align(CenterVertically)
        )
        if (item.daysOfWeek == WEEKDAYS) {
            Emphasized(stringResource(R.string.weekdays))
        }
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
    val frequencyCommitment = STM_FREQUENCY_COMMITMENT
    FrequencyCommitmentCard(frequencyCommitment)
}
