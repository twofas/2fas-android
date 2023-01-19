package com.twofasapp.notifications.ui

import androidx.lifecycle.viewModelScope
import com.twofasapp.base.BaseViewModel
import com.twofasapp.base.dispatcher.Dispatchers
import com.twofasapp.core.analytics.AnalyticsEvent
import com.twofasapp.core.analytics.AnalyticsParam
import com.twofasapp.core.analytics.AnalyticsService
import com.twofasapp.notifications.domain.GetNotificationsCase
import com.twofasapp.notifications.domain.ReadAllNotificationsCase
import com.twofasapp.notifications.domain.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class NotificationsViewModel(
    private val dispatchers: Dispatchers,
    private val getNotificationsCase: GetNotificationsCase,
    private val readAllNotificationsCase: ReadAllNotificationsCase,
    private val analyticsService: AnalyticsService,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatchers.io()) {
            val notifications = getNotificationsCase()
            _uiState.update { it.copy(items = notifications) }

            readAllNotificationsCase()
        }
    }

    fun itemClicked(notification: Notification) {
        viewModelScope.launch(dispatchers.io()) {
            analyticsService.captureEvent(
                AnalyticsEvent.NEWS_CLICK,
                AnalyticsParam.ID to notification.id,
            )
            _uiState.update {
                it.copy(items = it.items.map { item ->
                    if (item.id == notification.id) {
                        item.copy(isRead = true)
                    } else {
                        item
                    }
                })
            }

            _uiState.update { it.postEvent(NotificationsUiState.Event.OpenBrowser(notification.link)) }
        }
    }

    fun eventHandled(id: String) {
        _uiState.update { it.reduceEvent(id) }
    }
}