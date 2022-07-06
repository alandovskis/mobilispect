package com.mobilispect.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mobilispect.android.R
import com.mobilispect.android.ui.frequency.FrequencyCommitmentCard
import com.mobilispect.android.ui.theme.MobilispectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobilispectTheme {
                Scaffold(topBar = { TopBar() }) {
                    FrequencyCommitmentCard()
                }
            }
        }
    }

    @Composable
    private fun TopBar() {
        TopAppBar(title = {
            Text(
                text = stringResource(id = R.string.frequency_commitment),
                style = MaterialTheme.typography.h5
            )
        })
    }
}