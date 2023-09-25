package com.twofasapp.domain.notification

import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.twofasapp.common.ktx.runSafely
import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.feature.browserext.ui.request.BrowserExtRequestActivity
import com.twofasapp.feature.browserext.ui.requestapprove.BrowserExtRequestApproveActivity
import com.twofasapp.locale.R
import com.twofasapp.data.push.notification.ShowBrowserExtRequestNotification
import com.twofasapp.data.push.domain.Push
import com.twofasapp.data.push.notification.NotificationChannelProvider
import com.twofasapp.feature.browserext.notification.BrowserExtRequestPayload
import com.twofasapp.feature.browserext.notification.BrowserExtRequestReceiver
import com.twofasapp.feature.browserext.notification.DomainMatcher
import com.twofasapp.security.data.SecurityRepository
import com.twofasapp.security.domain.model.LockMethod

class ShowBrowserExtRequestNotificationImpl(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val notificationChannelProvider: NotificationChannelProvider,
    private val securityRepository: SecurityRepository,
    private val servicesRepository: ServicesRepository,
    private val browserExtRepository: BrowserExtRepository,
) : ShowBrowserExtRequestNotification {

    private val keyguardManager: KeyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    override suspend fun invoke(push: Push.BrowserExtRequest) {
        runSafely { browserExtRepository.fetchTokenRequests() }

        val domain = DomainMatcher.extractDomain(push.domain)
        val matchedServices = DomainMatcher.findMatchingDomain(servicesRepository.getServices(), domain)
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
            .setSmallIcon(com.twofasapp.designsystem.R.drawable.push_icon)
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
                        action = BrowserExtRequestPayload.Action.Deny,
                        notificationId = notificationId,
                        extensionId = push.extensionId,
                        requestId = push.requestId,
                        serviceId = serviceId,
                        domain = domain,
                    )

                    val allowIntent = createActionIntent(
                        action = BrowserExtRequestPayload.Action.Approve,
                        notificationId = notificationId,
                        extensionId = push.extensionId,
                        requestId = push.requestId,
                        serviceId = serviceId,
                        domain = domain,
                    )

                    val denyAction =
                        NotificationCompat.Action.Builder(
                            com.twofasapp.designsystem.R.drawable.ic_close,
                            context.getString(R.string.extension__deny),
                            denyIntent
                        ).build()
                    val allowAction =
                        NotificationCompat.Action.Builder(
                            com.twofasapp.designsystem.R.drawable.ic_done,
                            context.getString(R.string.extension__approve),
                            allowIntent
                        ).build()

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
        val contentIntent = Intent(context, BrowserExtRequestActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra(
                BrowserExtRequestPayload.Key, BrowserExtRequestPayload(
                    action = BrowserExtRequestPayload.Action.Approve,
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
        action: BrowserExtRequestPayload.Action,
        notificationId: Int,
        extensionId: String,
        requestId: String,
        serviceId: Long?,
        domain: String,
    ): PendingIntent {

        val payload = BrowserExtRequestPayload(
            action = action,
            notificationId = notificationId,
            extensionId = extensionId,
            requestId = requestId,
            serviceId = serviceId ?: -1,
            domain = domain,
        )

        val lockMethod = securityRepository.getLockMethod()

        return when {
            lockMethod != LockMethod.NoLock || keyguardManager.isKeyguardLocked -> {
                val contentIntent = Intent(context, BrowserExtRequestApproveActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

                    putExtra(
                        BrowserExtRequestPayload.Key, BrowserExtRequestPayload(
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
                    context,
                    notificationId + action.hashCode(),
                    contentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

            else -> {
                val notifyIntent = BrowserExtRequestReceiver.createIntent(context, payload)
                PendingIntent.getBroadcast(
                    context,
                    notificationId + action.hashCode(),
                    notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        }
    }
}
