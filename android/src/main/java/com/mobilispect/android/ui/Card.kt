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
fun Card(title: String? = null, modifier: Modifier = Modifier.padding(12.dp), content: @Composable () -> Unit) {
    androidx.compose.material.Card(
        backgroundColor = MaterialTheme.colors.secondaryVariant,
        contentColor = MaterialTheme.colors.onSecondary,
        modifier = Modifier
            .fillMaxWidth()
    )
    {
        Column(modifier = modifier) {
            if (title != null) {
                Text(text = title, style = MaterialTheme.typography.h6)
            }

            content()
        }
    }
}