package com.twofasapp.navigation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun RouterNavHost(router: Router<*>, viewModelStoreOwner: ViewModelStoreOwner? = null) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    NavHost(navController, startDestination = router.startDirection()) {
        router.registerGraph(scope, this, navController, viewModelStoreOwner)
    }
}

@Composable
fun withOwner(viewModelStoreOwner: ViewModelStoreOwner?, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalViewModelStoreOwner provides viewModelStoreOwner!!
    ) {
        content()
    }
}