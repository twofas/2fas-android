package com.twofasapp.designsystem.service

import android.app.Activity
import androidx.compose.ui.graphics.Color
import com.twofasapp.designsystem.ktx.copyToClipboard
import com.twofasapp.locale.R

data class ServiceState(
    val name: String,
    val info: String? = null,
    val code: String = "",
    val nextCode: String = "",
    val timer: Int = 0,
    val hotpCounter: Int? = null,
    val hotpCounterEnabled: Boolean = true,
    val progress: Float = 0f,
    val imageType: ServiceImageType,
    val authType: ServiceAuthType = ServiceAuthType.Totp,
    val iconLight: String,
    val iconDark: String,
    val labelText: String?,
    val labelColor: Color,
    val badgeColor: Color = Color.Unspecified,
) {

    fun copyToClipboard(activity: Activity, showNextToken: Boolean) {
        val copyNextToken = showNextToken && timer <= ServiceExpireTransitionThreshold

        activity.copyToClipboard(
            isSensitive = true,
            text = if (copyNextToken) {
                nextCode
            } else {
                code
            },
            toast = if (copyNextToken) {
                activity.getString(R.string.tokens__next_copied_clipboard)
            } else {
                activity.getString(R.string.tokens__copied_clipboard)
            }
        )
    }

    fun isNextCodeEnabled(showNextCode: Boolean): Boolean {
        return timer <= ServiceExpireTransitionThreshold && showNextCode && authType == ServiceAuthType.Totp
    }

    companion object {
        val Empty = ServiceState(
            name = "",
            info = null,
            code = "",
            nextCode = "",
            timer = 0,
            hotpCounter = null,
            hotpCounterEnabled = false,
            progress = 0.0f,
            imageType = ServiceImageType.Icon,
            authType = ServiceAuthType.Totp,
            iconLight = "",
            iconDark = "",
            labelText = null,
            labelColor = Color.Unspecified,
            badgeColor = Color.Unspecified,
        )
    }
}