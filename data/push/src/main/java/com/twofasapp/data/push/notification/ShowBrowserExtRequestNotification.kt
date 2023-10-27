package com.twofasapp.data.push.notification

import com.twofasapp.data.push.domain.Push

interface ShowBrowserExtRequestNotification {
    suspend operator fun invoke(push: Push.BrowserExtRequest)
}