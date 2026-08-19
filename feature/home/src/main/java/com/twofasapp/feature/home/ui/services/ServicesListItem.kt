package com.twofasapp.feature.home.ui.services

import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.foundation.lazy.ListItem
import com.twofasapp.data.services.domain.Group

sealed class ServicesListItem(
    key: Any,
    type: Any,
) : ListItem(key = key, type = type) {
    object Loader : ServicesListItem("Loader", "Loader")
    object Empty : ServicesListItem("Empty", "Empty")
    object EmptySearch : ServicesListItem("EmptySearch", "EmptySearch")
    object SyncNoticeBar : ServicesListItem("SyncNoticeBar", "SyncNoticeBar")
    object SyncReminder : ServicesListItem("SyncReminder", "SyncReminder")
    object AppReview : ServicesListItem("AppReview", "AppReview")
    object PassBanner : ServicesListItem("PassBanner", "PassBanner")
    data class ServiceItem(val service: Service) : ServicesListItem("Service:${service.id}", "Service")
    data class GroupItem(val group: Group) : ServicesListItem("Group:${group.id ?: "Default"}", "Group")
}