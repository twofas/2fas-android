package com.twofasapp.common.navigation

interface NavNode {
    val path: String

    fun route(graph: NavGraph): String {
        return "${graph.route}/$path"
    }
}