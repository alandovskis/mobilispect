package com.mobilispect.android.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun ScreenFrame(screenTitle: String, function: @Composable () -> Unit) {
    Scaffold(topBar = { TopBar(screenTitle) }) {
        function()
    }
}

@Composable
fun TopBar(screenTitle: String) {
    TopAppBar(title = {
        Text(
            text = screenTitle,
            style = MaterialTheme.typography.h5
        )
    })
}
