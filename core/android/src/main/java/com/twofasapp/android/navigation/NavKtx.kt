package com.twofasapp.android.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NamedNavArgument

@Composable
fun withOwner(viewModelStoreOwner: ViewModelStoreOwner?, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalViewModelStoreOwner provides viewModelStoreOwner!!
    ) {
        content()
    }
}

fun <T> SavedStateHandle.getOrThrow(key: String): T {
    return get<T>(key) ?: throw IllegalNavArgException(key)
}

fun <T> SavedStateHandle.getOrThrowNullable(key: String): T? {
    return get<T>(key)
}

internal fun String.replaceArgsInRoute(vararg args: Pair<NamedNavArgument, Any?>): String {
    var routeWithArgs = this
    args
        .filter { it.second != null }
        .forEach { arg -> routeWithArgs = routeWithArgs.replace("{${arg.first.name}}", arg.second.toString()) }
    return routeWithArgs
}

inline fun <reified T : Any> Context.intentFor(vararg params: Pair<String, Any?>): Intent =
    Intent(this, T::class.java).apply {
        putExtras(bundleOf(*params))
    }