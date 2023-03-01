package com.mobilispect.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mobilispect.common.util.Ranked

@Composable
fun <T : Ranked> RankedCard(
    title: String,
    subtitle: String,
    values: Iterable<T>,
    unit: String,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier, T) -> Unit
) {
    Card(title = title, subtitle = subtitle, modifier = modifier) {
        for ((index, item) in values.withIndex()) {
            RankedItem(
                index = index,
                rankedValues = item.range,
                rankedUnit = unit, item = item, content = content
            )
        }
    }
}
