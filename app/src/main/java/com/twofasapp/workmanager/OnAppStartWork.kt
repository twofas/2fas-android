package com.twofasapp.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.twofasapp.android.navigation.Screen
import com.twofasapp.data.browserext.BrowserExtRepository
import com.twofasapp.data.notifications.NotificationsRepository
import com.twofasapp.data.notifications.domain.Notification
import com.twofasapp.data.notifications.domain.PeriodicNotificationType
import com.twofasapp.data.services.BackupRepository
import com.twofasapp.data.services.ServicesRepository
import com.twofasapp.data.session.SessionRepository
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.time.Duration
import java.time.Instant
import java.util.UUID

class OnAppStartWork(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    companion object {
        fun dispatch(context: Context) {
            val request: OneTimeWorkRequest = OneTimeWorkRequestBuilder<OnAppStartWork>()
                .build()

            Timber.d("Append new OnAppStartWork")
            WorkManager.getInstance(context)
                .enqueueUniqueWork("OnAppStartWork", ExistingWorkPolicy.APPEND_OR_REPLACE, request)
        }
    }


    private val sessionRepository: SessionRepository by inject()
    private val notificationsRepository: NotificationsRepository by inject()
    private val backupRepository: BackupRepository by inject()
    private val servicesRepository: ServicesRepository by inject()
    private val browserExtRepository: BrowserExtRepository by inject()

    override suspend fun doWork(): Result {
        checkPeriodicNotifications()
        return Result.success()
    }

    private suspend fun checkPeriodicNotifications() {
        if (sessionRepository.isOnboardingDisplayed().not()) {
            Timber.d("This is first app launch -> do nothing")
            // This is first app launch -> do nothing
            return
        }

        val n = notificationsRepository.getPeriodicNotificationCounter()
        val timestampMillis = notificationsRepository.getPeriodicNotificationTimestamp()
        val timestamp = Instant.ofEpochMilli(timestampMillis)
        val now = Instant.now()

        Timber.d("n = $n")

        if (timestampMillis != 0L && now.isBefore(timestamp.plusSeconds(Duration.ofDays(30).toSeconds()))) {
            Timber.d("Less than 30 days")
            // Finish when last notification was triggered less than 30 days ago
            return
        }

        val notificationToShow = when (n) {
            -1 -> PeriodicNotificationType.TipsAndTricks
            0 -> PeriodicNotificationType.Backup
            1 -> PeriodicNotificationType.BrowserExtension
            2 -> PeriodicNotificationType.Donate
            else -> return
        }

        showNotification(notificationToShow)

        notificationsRepository.setPeriodicNotificationCounter(if (n == 2) 0 else n + 1)
        notificationsRepository.setPeriodicNotificationTimestamp(now.toEpochMilli())
    }

    private suspend fun showNotification(type: PeriodicNotificationType) {
        Timber.d("Show notification: $type")
        notificationsRepository.clearPeriodicNotifications()

        when (type) {
            PeriodicNotificationType.TipsAndTricks -> {
                notificationsRepository.insertPeriodicNotification(
                    type = type,
                    notification = createPeriodicNotification(
                        category = Notification.Category.Youtube,
                        message = context.getString(com.twofasapp.locale.R.string.periodic_notification_tips),
                        link = "https://2fas.com/2fasauth-tutorial",
                    )
                )
            }

            PeriodicNotificationType.Backup -> {
                if (servicesRepository.getServices().isNotEmpty() && backupRepository.observeCloudBackupStatus().first().active.not()) {
                    // User has services but backup is inactive
                    notificationsRepository.insertPeriodicNotification(
                        type = type,
                        notification = createPeriodicNotification(
                            category = Notification.Category.Updates,
                            message = context.getString(com.twofasapp.locale.R.string.periodic_notification_backup),
                            link = "",
                            internalRoute = Screen.Backup.route,
                        )
                    )
                }
            }

            PeriodicNotificationType.BrowserExtension -> {
                if (servicesRepository.getServices().isNotEmpty() && browserExtRepository.observePairedBrowsers().first().isEmpty()) {
                    notificationsRepository.insertPeriodicNotification(
                        type = type,
                        notification = createPeriodicNotification(
                            category = Notification.Category.News,
                            message = context.getString(com.twofasapp.locale.R.string.periodic_notification_browser_extension),
                            link = "https://2fas.com/browser-extension/",
                        )
                    )
                }
            }

            PeriodicNotificationType.Donate -> {
                if (servicesRepository.getServices().isNotEmpty()) {
                    notificationsRepository.insertPeriodicNotification(
                        type = type,
                        notification = createPeriodicNotification(
                            category = Notification.Category.Features,
                            message = context.getString(com.twofasapp.locale.R.string.periodic_notification_donate),
                            link = "https://2fas.com/donate/",
                        )
                    )
                }
            }
        }
    }

    private fun createPeriodicNotification(
        category: Notification.Category,
        message: String,
        link: String,
        internalRoute: String? = null,
    ): Notification {
        return Notification(
            id = UUID.randomUUID().toString(),
            category = category,
            link = link,
            message = message,
            publishTime = Instant.now().toEpochMilli(),
            push = false,
            platform = "android",
            isRead = false,
            internalRoute = internalRoute,
        )
    }
}