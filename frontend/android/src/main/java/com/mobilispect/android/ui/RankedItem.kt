@file:Suppress("MagicNumber")

package com.mobilispect.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobilispect.android.ui.previews.ThemePreviews
import com.mobilispect.android.ui.theme.MobilispectTheme

@Composable
fun RankedItem(
    index: Int,
    rankedValue: Int,
    rankedUnit: String,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    Row(
        modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Rank(index = index, modifier = Modifier.fillMaxWidth(0.1f))
        content(Modifier.fillMaxWidth(0.5f))
        RankedValue(
            value = rankedValue,
            unit = rankedUnit,
            modifier = Modifier.fillMaxWidth(0.4f)
        )
    }
}

@Composable
private fun Rank(index: Int, modifier: Modifier = Modifier) {
    Text(text = "${index + 1}", style = MaterialTheme.typography.displaySmall, modifier = modifier)
}

@Composable
private fun RankedValue(
    value: Number,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(
            text = value.toString(), modifier = Modifier
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(5.dp), style = MaterialTheme.typography.displaySmall
        )
        Text(
            text = unit.uppercase(), style = MaterialTheme.typography.labelLarge
        )
    }
}

@ThemePreviews
@Composable
fun PreviewRankedItem() {
    MobilispectTheme {
        RankedItem(
            index = 1,
            rankedValue = 5,
            rankedUnit = "Minutes"
        ) { modifier ->
            Text(text = "Main Street", modifier = modifier)
        }
    }
}
