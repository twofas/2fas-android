package com.twofasapp.push.domain

import com.twofasapp.push.domain.model.Push

interface ShowBrowserExtRequestNotificationCase {
    suspend operator fun invoke(push: Push.BrowserExtRequest)
}