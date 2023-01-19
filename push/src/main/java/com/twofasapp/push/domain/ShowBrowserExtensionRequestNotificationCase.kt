package com.twofasapp.push.domain

import com.twofasapp.push.domain.model.Push

interface ShowBrowserExtensionRequestNotificationCase {
    suspend operator fun invoke(push: Push.BrowserExtensionRequest)
}