package com.mobilispect.mobile.android.ui.routes

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobilispect.mobile.android.ui.Card
import com.mobilispect.android.ui.LoadingCard
import com.mobilispect.android.ui.OutlinedButton
import com.mobilispect.mobile.android.ui.ScreenFrame
import com.mobilispect.android.ui.previews.ThemePreviews
import com.mobilispect.android.ui.theme.MobilispectTheme
import com.mobilispect.mobile.android.R

@Composable
fun RoutesListRoute(
    viewModel: RoutesListViewModel = hiltViewModel()
) {
    viewModel.sync()
    val uiState by viewModel.uiState.collectAsState()
    RouteListScreen(uiState)
}

@Composable
fun RouteListScreen(uiState: RouteListUIState, modifier: Modifier = Modifier) {
    ScreenFrame(screenTitle = stringResource(R.string.routes)) {
        Card(modifier = modifier) {
            when (uiState) {
                Loading -> LoadingCard()
                is RoutesFound -> {
                    LazyColumn {
                        for (route in uiState.routes)
                            item {
                                OutlinedButton(onClick = { /*TODO*/ }) {
                                    Text("${route.shortName}: ${route.longName}")
                                }
                            }
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun PreviewRoutesListScreen() {
    MobilispectTheme {
        RouteListScreen(
            uiState = RoutesFound(
                routes = listOf(
                    RouteUIState(
                        id = "r-abcd-a",
                        shortName = "1",
                        longName = "Main Street",
                        agencyID = "r-abcd-a"
                    )
                )
            )
        )

    }
}
