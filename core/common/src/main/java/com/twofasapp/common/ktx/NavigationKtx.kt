package com.twofasapp.common.ktx

import androidx.navigation.NavController

fun NavController.clearGraphBackStack() {
    currentBackStackEntry?.destination?.parent?.route?.let { currentGraphRoute ->
        popBackStack(currentGraphRoute, inclusive = true)
    }
}