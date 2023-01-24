package com.twofasapp.feature.home.ui.services

import com.twofasapp.designsystem.lazy.ListItem

sealed class ServicesListItem(
    override val key: Any,
    override val type: Any,
) : ListItem {
    object Loader : ServicesListItem("loader", "loader")
    object Empty : ServicesListItem("empty", "empty")
    data class Service(val id: Long) : ServicesListItem("service_$id", "service")
    data class Group(val id: Long) : ServicesListItem("group_$id", "group")
}