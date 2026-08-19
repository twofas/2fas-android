package com.twofasapp.ui.main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen

internal class AppNavigator : Navigator {

    val backStack: SnapshotStateList<Screen> = mutableStateListOf()

    fun setStartRoot(screen: Screen) {
        if (backStack.isEmpty()) {
            backStack.add(screen)
        }
    }

    override fun navigate(screen: Screen) {
        backStack.add(screen)
    }

    override fun back(): Boolean {
        return if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            true
        } else {
            false
        }
    }

    override fun resetTo(screen: Screen) {
        backStack.clear()
        backStack.add(screen)
    }

    override fun popTo(screen: Screen, inclusive: Boolean) {
        val index = backStack.indexOfLast { it == screen }
        if (index == -1) return

        val lastToKeep = if (inclusive) index - 1 else index
        while (backStack.lastIndex > lastToKeep) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
}