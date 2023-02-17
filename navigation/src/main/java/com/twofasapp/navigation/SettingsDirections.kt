package com.twofasapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import com.twofasapp.navigation.base.Directions

sealed interface SettingsDirections : Directions {
    object GoBack : SettingsDirections
    object Main : SettingsDirections
    object Theme : SettingsDirections
    object BrowserExtension : SettingsDirections
    class BrowserDetails(val extensionId: String) : SettingsDirections
    object PairingScan : SettingsDirections
    object Permission : SettingsDirections
    class PairingProgress(val extensionId: String) : SettingsDirections
}