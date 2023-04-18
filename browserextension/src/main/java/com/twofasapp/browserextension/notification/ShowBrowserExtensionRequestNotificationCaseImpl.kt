package com.twofasapp.browserextension.notification

import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.twofasapp.browserextension.ui.request.BrowserExtensionRequestActivity
import com.twofasapp.browserextension.ui.request.BrowserExtensionRequestApproveActivity
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.push.domain.ShowBrowserExtensionRequestNotificationCase
import com.twofasapp.push.domain.model.Push
import com.twofasapp.push.notification.NotificationChannelProvider
import com.twofasapp.resources.R
import com.twofasapp.security.domain.GetLockMethodCase
import com.twofasapp.security.domain.model.LockMethod
import com.twofasapp.services.domain.GetServicesCase

class ShowBrowserExtensionRequestNotificationCaseImpl(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val notificationChannelProvider: NotificationChannelProvider,
    private val getLockMethodCase: GetLockMethodCase,
    private val getServicesCase: GetServicesCase,
    private val browserExtRepository: BrowserExtRepository,
) : ShowBrowserExtensionRequestNotificationCase {

    private val keyguardManager: KeyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    override suspend fun invoke(push: Push.BrowserExtensionRequest) {
        runSafely { browserExtRepository.fetchTokenRequests() }

        val domain = DomainMatcher.extractDomain(push.domain)
        val matchedServices = DomainMatcher.findServicesMatchingDomain(getServicesCase(), domain)
        val isOneDomainMatched = matchedServices.size == 1
        val notificationId = push.requestId.hashCode()
        val serviceId = if (matchedServices.size == 1) matchedServices.first().id else null

        val notification = NotificationCompat.Builder(context, notificationChannelProvider.getBrowserExtensionChannelId())
            .setContentTitle(context.getString(R.string.browser__2fa_token_request_title))
            .setContentText(context.getString(R.string.browser__2fa_token_request_content).plus(" $domain?"))
            .setStyle(NotificationCompat.BigTextStyle())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.push_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(
                createContentIntent(
                    notificationId = notificationId,
                    extensionId = push.extensionId,
                    requestId = push.requestId,
                    serviceId = serviceId,
                    domain = domain,
                )
            )
            .apply {
                if (isOneDomainMatched) {

                    val denyIntent = createActionIntent(
                        action = BrowserExtensionRequestPayload.Action.Deny,
                        notificationId = notificationId,
                        extensionId = push.extensionId,
                        requestId = push.requestId,
                        serviceId = serviceId,
                        domain = domain,
                    )

                    val allowIntent = createActionIntent(
                        action = BrowserExtensionRequestPayload.Action.Approve,
                        notificationId = notificationId,
                        extensionId = push.extensionId,
                        requestId = push.requestId,
                        serviceId = serviceId,
                        domain = domain,
                    )

                    val denyAction =
                        NotificationCompat.Action.Builder(R.drawable.ic_action_deny, context.getString(R.string.extension__deny), denyIntent).build()
                    val allowAction =
                        NotificationCompat.Action.Builder(R.drawable.ic_action_allow, context.getString(R.string.extension__approve), allowIntent).build()

                    addAction(denyAction)
                    addAction(allowAction)
                }
            }
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun createContentIntent(
        notificationId: Int,
        extensionId: String,
        requestId: String,
        serviceId: Long?,
        domain: String,
    ): PendingIntent {
        val contentIntent = Intent(context, BrowserExtensionRequestActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra(
                BrowserExtensionRequestPayload.Key, BrowserExtensionRequestPayload(
                    action = BrowserExtensionRequestPayload.Action.Approve,
                    notificationId = notificationId,
                    extensionId = extensionId,
                    requestId = requestId,
                    serviceId = serviceId ?: -1,
                    domain = domain,
                )
            )
        }

        return PendingIntent.getActivity(
            context, notificationId, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createActionIntent(
        action: BrowserExtensionRequestPayload.Action,
        notificationId: Int,
        extensionId: String,
        requestId: String,
        serviceId: Long?,
        domain: String,
    ): PendingIntent {

        val payload = BrowserExtensionRequestPayload(
            action = action,
            notificationId = notificationId,
            extensionId = extensionId,
            requestId = requestId,
            serviceId = serviceId ?: -1,
            domain = domain,
        )

        val lockMethod = getLockMethodCase()

        return when {
            lockMethod != LockMethod.NoLock || keyguardManager.isKeyguardLocked -> {
                val contentIntent = Intent(context, BrowserExtensionRequestApproveActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

                    putExtra(
                        BrowserExtensionRequestPayload.Key, BrowserExtensionRequestPayload(
                            action = action,
                            notificationId = notificationId,
                            extensionId = extensionId,
                            requestId = requestId,
                            serviceId = serviceId ?: -1,
                            domain = domain,
                        )
                    )
                }

                PendingIntent.getActivity(
                    context, notificationId + action.hashCode(), contentIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

            else -> {
                val notifyIntent = BrowserExtensionRequestReceiver.createIntent(context, payload)
                PendingIntent.getBroadcast(
                    context, notificationId + action.hashCode(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        }
    }
}
