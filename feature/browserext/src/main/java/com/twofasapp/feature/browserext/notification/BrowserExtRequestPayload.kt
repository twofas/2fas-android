package com.twofasapp.feature.browserext.notification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class BrowserExtRequestPayload(
    val action: Action,
    val notificationId: Int,
    val extensionId: String,
    val requestId: String,
    val serviceId: Long = -1,
    val domain: String? = null,
) : Parcelable {

    companion object {
        const val Key = "BrowserExtRequestPayload"
    }

    enum class Action {
        Approve, Deny,
    }
}