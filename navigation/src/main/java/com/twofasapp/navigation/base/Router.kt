package com.twofasapp.navigation.base

import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class Router<T : Directions> {

    private inner class InternalCommand(
        val direction: T,
    )

    private val commands: MutableSharedFlow<InternalCommand> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun registerGraph(
        scope: CoroutineScope,
        builder: NavGraphBuilder,
        navController: NavHostController,
        viewModelStoreOwner: ViewModelStoreOwner? = null
    ) {
        buildNavGraph(builder, viewModelStoreOwner)
        scope.launch {
            commands.collect {
                navigate(
                    navController,
                    it.direction,
                )
            }
        }
    }

    fun navigate(direction: T) {
        commands.tryEmit(
            InternalCommand(
                direction = direction,
            )
        )
    }

    abstract fun navigateBack()

    abstract fun startDirection(): String
    protected abstract fun buildNavGraph(builder: NavGraphBuilder, viewModelStoreOwner: ViewModelStoreOwner? = null)
    protected abstract fun navigate(navController: NavHostController, direction: T)
}