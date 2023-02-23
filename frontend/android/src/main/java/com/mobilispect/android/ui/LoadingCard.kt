@file:Suppress(
    "FunctionNaming",
)

package com.mobilispect.android.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mobilispect.android.ui.theme.MobilispectTheme

@Composable
fun LoadingCard(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .size(100.dp)
            .testTag("loading"),
        color = MaterialTheme.colors.primary,
    )
}

@Previews
@Composable
fun PreviewLoadingCard() {
    MobilispectTheme {
        LoadingCard()
    }
}
