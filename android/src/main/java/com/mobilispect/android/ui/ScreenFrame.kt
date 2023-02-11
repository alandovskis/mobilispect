@file:Suppress("FunctionNaming")

package com.mobilispect.android.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.mobilispect.android.ui.theme.MobilispectTheme

@Composable
fun ScreenFrame(
    screenTitle: String,
    modifier: Modifier = Modifier,
    function: @Composable (Modifier) -> Unit
) {
    Scaffold(topBar = { TopBar(screenTitle, Modifier.testTag("title")) }) { padding ->
        function(modifier.padding(padding))
    }
}

@Composable
fun TopBar(screenTitle: String, modifier: Modifier = Modifier) {
    TopAppBar(backgroundColor = MaterialTheme.colors.primary, title = {
        Text(
            text = screenTitle,
            style = MaterialTheme.typography.h5,
            modifier = modifier.testTag("title")
        )
    })
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewScreenFrame() {
    MobilispectTheme {
        ScreenFrame(screenTitle = "Title") {
        }
    }
}
