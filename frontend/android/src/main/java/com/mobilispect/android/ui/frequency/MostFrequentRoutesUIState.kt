package com.mobilispect.android.ui.frequency

import kotlinx.datetime.DateTimePeriod

class MostFrequentRoutesUIState(val routes: Array<Route>) {
    data class Route(val shortName: String, val longName: String, val frequency: DateTimePeriod)
}
