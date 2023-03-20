package com.twofasapp.feature.home.ui.services

import com.twofasapp.designsystem.lazy.ListItem

sealed class ServicesListItem(
    override val key: Any,
    override val type: Any,
) : ListItem {
    object Loader : ServicesListItem("Loader", "Loader")
    object Empty : ServicesListItem("Empty", "Empty")
    object EmptySearch : ServicesListItem("EmptySearch", "EmptySearch")
    object SyncNoticeBar : ServicesListItem("SyncNoticeBar", "SyncNoticeBar")
    object SyncReminder : ServicesListItem("SyncReminder", "SyncReminder")
    data class Service(val id: Long) : ServicesListItem("Service:$id", "Service")
    data class Group(val id: String?) : ServicesListItem("Group:${id ?: "Default"}", "Group")
}