@file:Suppress(
    "FunctionNaming",
)

package com.mobilispect.android.ui.agencies

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobilispect.android.R
import com.mobilispect.android.ui.Card
import com.mobilispect.android.ui.LoadingCard
import com.mobilispect.android.ui.OutlinedButton
import com.mobilispect.android.ui.ScreenFrame

@Composable
fun AgenciesRoute(
    viewModel: AgenciesViewModel = hiltViewModel(),
    navigateToCommitment: (String) -> Unit
) {
    viewModel.sync()
    val uiState by viewModel.uiState.collectAsState(initial = Loading)
    AgenciesScreen(uiState = uiState, navigateToCommitment = navigateToCommitment)
}

@Composable
fun AgenciesScreen(
    uiState: AgenciesUIState,
    modifier: Modifier = Modifier,
    navigateToCommitment: (String) -> Unit
) {
    ScreenFrame(screenTitle = stringResource(id = R.string.agencies), modifier = modifier) {
        when (uiState) {
            Loading -> LoadingCard()
            NoAgencyFound -> {
                Card {
                    Text(stringResource(id = R.string.no_agencies_found))
                }
            }
            is AgenciesFound -> {
                Card {
                    LazyColumn {
                        for (agency in uiState.agencies) {
                            item {
                                OutlinedButton(
                                    onClick = { navigateToCommitment(agency.id) },
                                ) {
                                    Text(
                                        text = agency.name,
                                        style = MaterialTheme.typography.button
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
