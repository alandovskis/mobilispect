@file:Suppress("FunctionNaming")
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mobilispect.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.mobilispect.android.ui.previews.ThemePreviews
import com.mobilispect.android.ui.theme.MobilispectTheme

@Composable
fun ScreenFrame(
    screenTitle: String,
    modifier: Modifier = Modifier,
    function: @Composable () -> Unit
) {
    Scaffold(topBar = { TopBar(screenTitle, Modifier.testTag("title")) }) { padding ->
        Surface(modifier = modifier.padding(padding), color = MaterialTheme.colorScheme.surface) {
            function()
        }
    }
}

@Composable
fun TopBar(screenTitle: String, modifier: Modifier = Modifier) {
    TopAppBar(colors = TopAppBarDefaults.smallTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.primary)
    ),
        title = {
            Text(
                text = screenTitle,
                modifier = modifier.testTag("title"),
                style = MaterialTheme.typography.titleLarge
            )
        })
}

@ThemePreviews
@Composable
fun PreviewScreenFrame() {
    MobilispectTheme {
        ScreenFrame(screenTitle = "Title") {
        }
    }
}
