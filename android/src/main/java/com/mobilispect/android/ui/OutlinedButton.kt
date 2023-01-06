package com.mobilispect.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedButton(
    onClick: () -> Unit,
    text: String
) {
    androidx.compose.material.OutlinedButton(
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSecondary),
        onClick = onClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button
        )
    }
}