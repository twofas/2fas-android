package com.twofasapp.common.ktx

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.navigation.NavController

fun NavController.clearGraphBackStack() {
    currentBackStackEntry?.destination?.parent?.route?.let { currentGraphRoute ->
        popBackStack(currentGraphRoute, inclusive = true)
    }
}

inline fun <reified T : Any> Context.intentFor(vararg params: Pair<String, Any?>): Intent =
    Intent(this, T::class.java).apply {
        putExtras(bundleOf(*params))
    }