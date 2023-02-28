@file:Suppress("MagicNumber")

package com.mobilispect.android.ui.frequency

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mobilispect.android.R
import com.mobilispect.android.ui.Card
import com.mobilispect.android.ui.RankedItem
import com.mobilispect.android.ui.previews.ThemePreviews
import com.mobilispect.android.ui.theme.MobilispectTheme
import kotlinx.datetime.DateTimePeriod

/**
 * The most frequent routes by 7-day median frequency.
 */
@Composable
fun MostFrequentRoutesCard(uiState: MostFrequentRoutesUIState, modifier: Modifier = Modifier) {
    Card(title = "Most Frequent Routes", subtitle = "by Median Frequency", modifier = modifier) {
        for ((index, route) in uiState.routes.withIndex()) {
            RankedItem(
                index = index,
                rankedValue = route.frequency.minutes,
                rankedUnit = stringResource(id = R.string.minutes),
            ) { subModifier ->
                RouteName(route = route, modifier = subModifier.fillMaxWidth(0.5f))
            }
        }
    }
}

@Composable
private fun RouteName(route: MostFrequentRoutesUIState.Route, modifier: Modifier = Modifier) {
    Text(
        text = "${route.shortName} ${route.longName}",
        modifier = modifier.padding(horizontal = 5.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}

@ThemePreviews
@Composable
fun PreviewMostFrequentRoutesCard() {
    MobilispectTheme {
        MostFrequentRoutesCard(
            uiState = MostFrequentRoutesUIState(
                routes = arrayOf(
                    MostFrequentRoutesUIState.Route(
                        shortName = "1",
                        longName = "Main Street",
                        frequency = DateTimePeriod(minutes = 5)
                    ), MostFrequentRoutesUIState.Route(
                        shortName = "2",
                        longName = "Central Avenue",
                        frequency = DateTimePeriod(minutes = 10)
                    )
                )
            )
        )
    }
}
