package com.twofasapp.ui.main

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.twofasapp.feature.browserext.notification.BrowserExtRequestPayload
import com.twofasapp.feature.browserext.notification.BrowserExtRequestReceiver
import com.twofasapp.data.browserext.domain.TokenRequest
import com.twofasapp.designsystem.dialog.ConfirmDialog
import com.twofasapp.designsystem.ktx.currentActivity
import com.twofasapp.feature.browserext.ui.request.BrowserExtRequestActivity
import com.twofasapp.locale.TwLocale

@Composable
internal fun BrowserExtRequestDialog(
    onRequestHandled: () -> Unit = {},
    browserExtRequest: BrowserExtRequest,
) {
    val activity = LocalContext.currentActivity

    ConfirmDialog(
        onDismissRequest = {},
        title = TwLocale.strings.tokenRequestTitle,
        body = TwLocale.strings.tokenRequestBody.plus("${browserExtRequest.domain}?"),
        positive = TwLocale.strings.commonApprove,
        negative = TwLocale.strings.commonDeny,
        onPositive = {
            val isOneDomainMatched =
                browserExtRequest.matchedServices.size == 1
            val serviceId =
                if (browserExtRequest.matchedServices.size == 1) browserExtRequest.matchedServices.first().id else null

            if (isOneDomainMatched) {
                val payload =
                    BrowserExtRequestPayload(
                        action = BrowserExtRequestPayload.Action.Approve,
                        notificationId = -1,
                        extensionId = browserExtRequest.request.extensionId,
                        requestId = browserExtRequest.request.requestId,
                        serviceId = serviceId ?: -1,
                        domain = browserExtRequest.domain,
                    )
                activity.sendBroadcast(
                    BrowserExtRequestReceiver.createIntent(activity, payload)
                )

                onRequestHandled.invoke()
            } else {

                val contentIntent = Intent(
                    activity,
                    BrowserExtRequestActivity::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

                    putExtra(
                        BrowserExtRequestPayload.Key,
                        BrowserExtRequestPayload(
                            action = BrowserExtRequestPayload.Action.Approve,
                            notificationId = -1,
                            extensionId = browserExtRequest.request.extensionId,
                            requestId = browserExtRequest.request.requestId,
                            serviceId = -1,
                            domain = browserExtRequest.domain,
                        )
                    )
                }

                activity.startActivity(contentIntent)
            }
        },
        onNegative = {
            val payload = BrowserExtRequestPayload(
                action = BrowserExtRequestPayload.Action.Deny,
                notificationId = -1,
                extensionId = browserExtRequest.request.extensionId,
                requestId = browserExtRequest.request.requestId,
                serviceId = -1,
                domain = browserExtRequest.domain,
            )

            activity.sendBroadcast(
                BrowserExtRequestReceiver.createIntent(activity, payload)
            )

            onRequestHandled.invoke()
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )
    )
}

@Preview
@Composable
private fun Preview() {
    BrowserExtRequestDialog(
        browserExtRequest = BrowserExtRequest(
            request = TokenRequest(
                domain = "Antionette",
                requestId = "Cristian",
                extensionId = "Kalia"
            ),
            domain = "Darleen",
            matchedServices = listOf()
        )
    )
}