package com.twofasapp.navigation

import com.twofasapp.navigation.base.Directions

sealed interface SecurityDirections : Directions {
    object GoBack : SecurityDirections
    object Security : SecurityDirections
    object SetupPin : SecurityDirections
    object DisablePin : SecurityDirections
    object ChangePin : SecurityDirections
}