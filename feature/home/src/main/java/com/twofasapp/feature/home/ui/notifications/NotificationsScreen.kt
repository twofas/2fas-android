package com.twofasapp.feature.home.ui.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.screen.EmptyScreen
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.openSafely
import com.twofasapp.data.notifications.domain.Notification
import com.twofasapp.feature.home.R
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun NotificationsScreen(
    viewModel: NotificationsViewModel = koinViewModel(),
    openInternalRoute: (String) -> Unit,
) {
    val notifications by viewModel.notificationsList.collectAsStateWithLifecycle()

    ScreenContent(
        notifications = notifications,
        onNotificationClick = { viewModel.onNotificationClick(it) },
        onInternalRouteClick = openInternalRoute,
    )
}

@Composable
private fun ScreenContent(
    notifications: List<Notification>,
    onNotificationClick: (Notification) -> Unit,
    onInternalRouteClick: (String) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = MdtLocale.strings.notificationsTitle) },
    ) { padding ->

        LazyColumn(Modifier.padding(padding)) {
            if (notifications.isEmpty()) {
                item {
                    EmptyScreen(
                        body = MdtLocale.strings.notificationsEmpty,
                        image = painterResource(id = R.drawable.img_notifications_empty),
                        modifier = Modifier.fillParentMaxSize(),
                    )
                }

                return@LazyColumn
            }

            items(notifications, key = { it.id }) { notification ->
                Notification(
                    notification = notification,
                    modifier = Modifier
                        .clickable(
                            notification.link.isNotBlank() || notification.internalRoute
                                .isNullOrBlank()
                                .not(),
                        ) {
                            onNotificationClick(notification)

                            if (notification.link.isNotBlank()) {
                                uriHandler.openSafely(notification.link, context)
                            }

                            if (notification.internalRoute
                                    .isNullOrBlank()
                                    .not()
                            ) {
                                onInternalRouteClick(notification.internalRoute.orEmpty())
                            }
                        }
                        .background(if (notification.isRead) MdtTheme.color.background else MdtTheme.color.surface)
                        .padding(16.dp),
                )
                HorizontalDivider(color = MdtTheme.color.divider)
            }
        }
    }
}

@Composable
private fun Notification(
    notification: Notification,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Image(
            painter = painterResource(
                when (notification.category) {
                    Notification.Category.Updates -> R.drawable.notif_category_update
                    Notification.Category.News -> R.drawable.notif_category_news
                    Notification.Category.Features -> R.drawable.notif_category_feature
                    Notification.Category.Youtube -> R.drawable.notif_category_video
                    Notification.Category.Tips -> R.drawable.notif_category_tips
                },
            ),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(top = 6.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = notification.message,
                modifier = Modifier.fillMaxWidth(),
                color = MdtTheme.color.onSurface,
                style = MdtTheme.typo.sm.normal,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = MdtLocale.formatDuration(notification.createdAt),
                modifier = Modifier.fillMaxWidth(),
                color = MdtTheme.color.onSurfaceVariant,
                style = MdtTheme.typo.xs.normal,
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        if (notification.link.isNotBlank()) {
            Icon(painter = MdtIcons.ExternalLink, contentDescription = null, tint = MdtTheme.color.iconTint)
        }
    }
}