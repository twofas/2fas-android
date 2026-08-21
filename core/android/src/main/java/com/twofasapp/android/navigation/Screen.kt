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
    data object Security : Screen

    @Serializable
    data object SetupPin : Screen

    @Serializable
    data object DisablePin : Screen

    @Serializable
    data object ChangePin : Screen

    @Serializable
    data object Trash : Screen

    @Serializable
    data object Backup : Screen

    @Serializable
    data object BackupSettings : Screen

    @Serializable
    data object About : Screen

    @Serializable
    data object AboutLicenses : Screen
}