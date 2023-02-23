@file:Suppress("FunctionNaming")

package com.mobilispect.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobilispect.android.ui.theme.MobilispectTheme

@Composable
fun Card(modifier: Modifier = Modifier, title: String? = null, content: @Composable () -> Unit = {}) {
    androidx.compose.material3.Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
    {
        Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
            if (title != null) {
                Text(text = title)
            }

            content()
        }
    }
}

@Previews
@Composable
fun PreviewCard() {
    MobilispectTheme {
        Card(title = "Test")
    }
}