package com.twofasapp.feature.home.ui.services

import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.foundation.lazy.ListItem
import com.twofasapp.data.services.domain.Group

sealed class HomeListItem(
    key: Any,
    type: Any,
) : ListItem(key = key, type = type) {
    object Loader : HomeListItem("Loader", "Loader")
    object Empty : HomeListItem("Empty", "Empty")
    object EmptySearch : HomeListItem("EmptySearch", "EmptySearch")
    object SyncNoticeBar : HomeListItem("SyncNoticeBar", "SyncNoticeBar")
    object SyncReminder : HomeListItem("SyncReminder", "SyncReminder")
    object AppReview : HomeListItem("AppReview", "AppReview")
    object PassBanner : HomeListItem("PassBanner", "PassBanner")
    data class ServiceItem(val service: Service) : HomeListItem("Service:${service.id}", "Service")
    data class GroupItem(val group: Group) : HomeListItem("Group:${group.id ?: "Default"}", "Group")
}