package com.twofasapp.android.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BottomBarState {
    private val visibleFlow = MutableStateFlow(true)

    val visible: StateFlow<Boolean> = visibleFlow

    fun setVisible(visible: Boolean) {
        visibleFlow.value = visible
    }
}