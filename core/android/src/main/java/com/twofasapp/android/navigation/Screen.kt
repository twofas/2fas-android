package com.twofasapp.android.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {

    @Serializable
    data object Startup : Screen

    @Serializable
    data object Developer : Screen

    @Serializable
    data object Services : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object Customization : Screen

    @Serializable
    data object Trash : Screen
}