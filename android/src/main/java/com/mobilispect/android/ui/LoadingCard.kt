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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilispect.android.ui.theme.MobilispectTheme

@Composable
fun LoadingCard() {
    CircularProgressIndicator(
        modifier = Modifier.size(100.dp),
        color = MaterialTheme.colors.primary
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
