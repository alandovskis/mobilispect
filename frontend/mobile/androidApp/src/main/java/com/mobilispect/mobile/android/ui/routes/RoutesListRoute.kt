package com.mobilispect.mobile.android.ui.routes

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mobilispect.mobile.android.ui.Card
import com.mobilispect.android.ui.LoadingCard
import com.mobilispect.android.ui.OutlinedButton
import com.mobilispect.mobile.android.ui.ScreenFrame
import com.mobilispect.android.ui.previews.ThemePreviews
import com.mobilispect.mobile.android.R
import com.mobilispect.mobile.android.ui.theme.MobilispectTheme
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
class RoutesList(val agencyID: String)

fun NavGraphBuilder.routesGraph(navigateToFrequencyViolations: (String) -> Unit) {
    composable<RoutesList> {
        RoutesListRoute(navigateToFrequencyViolations)
    }
}

@Composable
fun RoutesListRoute(navigateToFrequencyViolations: (String) -> Unit) {
    val viewModel: RoutesListViewModel = koinViewModel()
    viewModel.sync()
    val uiState by viewModel.uiState.collectAsState()
    RouteListScreen(uiState, navigateToFrequencyViolations)
}

@Composable
fun RouteListScreen(
    uiState: RouteListUIState,
    navigateToFrequencyViolations: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenFrame(screenTitle = stringResource(R.string.routes)) {
        Card(modifier = modifier) {
            when (uiState) {
                Loading -> LoadingCard()
                is RoutesFound -> {
                    LazyColumn {
                        for (route in uiState.routes)
                            item {
                                OutlinedButton(onClick = { navigateToFrequencyViolations(route.id)}) {
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
            ), navigateToFrequencyViolations = {}
        )

    }
}
