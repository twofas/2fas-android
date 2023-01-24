package com.twofasapp.navigation

import com.twofasapp.navigation.base.Directions

sealed interface StartDirections : Directions {
    object Main : StartDirections
}