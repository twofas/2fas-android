package com.twofasapp.common.navigation

import androidx.navigation.NamedNavArgument

interface NavNode {
    val path: String
    val graph: NavGraph

    val route: String
        get() = "${graph.route}/$path"
}

fun <T> String.withArg(arg: NamedNavArgument, value: T): String {
    return replace("{${arg.name}}", value.toString())
}