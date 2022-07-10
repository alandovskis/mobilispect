package com.mobilispect.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Item(content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colors.secondary,
        contentColor = MaterialTheme.colors.onSecondary,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
    ) {
        content()
    }
}