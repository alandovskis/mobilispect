@file:Suppress("FunctionNaming")

package com.mobilispect.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Card(modifier: Modifier = Modifier, title: String? = null, content: @Composable () -> Unit) {
    androidx.compose.material.Card(
        backgroundColor = MaterialTheme.colors.secondary,
        contentColor = MaterialTheme.colors.onSecondary,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
    {
        Column(modifier = modifier.padding(start = 8.dp, end = 8.dp)) {
            if (title != null) {
                Text(text = title, style = MaterialTheme.typography.h6)
            }

            content()
        }
    }
}