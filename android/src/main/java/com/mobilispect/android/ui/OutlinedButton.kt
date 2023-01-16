@file:Suppress("FunctionNaming")

package com.mobilispect.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedButton(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    androidx.compose.material.OutlinedButton(
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSecondary),
        onClick = onClick,
        content = content
    )
}
