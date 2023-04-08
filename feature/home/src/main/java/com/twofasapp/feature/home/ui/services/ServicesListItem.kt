package com.twofasapp.feature.home.ui.services

import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.domain.Service
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
    data class ServiceItem(val service: Service) : ServicesListItem("Service:${service.id}", "Service")
    data class GroupItem(val group: Group) : ServicesListItem("Group:${group.id ?: "Default"}", "Group")
}