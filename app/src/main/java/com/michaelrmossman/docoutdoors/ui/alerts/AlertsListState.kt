package com.michaelrmossman.docoutdoors.ui.alerts

import com.michaelrmossman.docoutdoors.enums.SearchBy
import com.michaelrmossman.docoutdoors.interfaces.AlertsUiState
import com.michaelrmossman.docoutdoors.model.Alert

data class AlertsListState(
    val alertsList: List<Alert>     = emptyList(),
    val alertState: AlertsUiState   = AlertsUiState.Loading,
    val searchBy  : SearchBy        = SearchBy.Name
)