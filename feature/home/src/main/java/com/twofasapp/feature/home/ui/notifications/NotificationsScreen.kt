package com.twofasapp.feature.home.ui.notifications

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.other.DotBadge
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.outline.HorizontalLine
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.screen.EmptyScreen
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.openSafely
import com.twofasapp.data.notifications.domain.Notification
import com.twofasapp.feature.home.R
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun NotificationsScreen(
    viewModel: NotificationsViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val notifications by viewModel.notificationsList.collectAsStateWithLifecycle()

    ScreenContent(
        notifications = notifications,
        onNotificationClick = { viewModel.onNotificationClick(it) },
        onInternalRouteClick = { route ->
            when (route) {
                Notification.InternalRoute.Backup -> navigator.open(Screen.Backup)
                else -> Unit
            }
        },
    )
}

@Composable
private fun ScreenContent(
    notifications: List<Notification>,
    onNotificationClick: (Notification) -> Unit,
    onInternalRouteClick: (Notification.InternalRoute) -> Unit,
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
                            notification.link.isNotBlank() || notification.internalRoute != null,
                        ) {
                            onNotificationClick(notification)

                            if (notification.link.isNotBlank()) {
                                uriHandler.openSafely(notification.link, context)
                            }

                            notification.internalRoute?.let { onInternalRouteClick(it) }
                        }
                        .background(MdtTheme.color.background),
                )

                HorizontalLine()
            }
        }
    }
}

@Composable
private fun Notification(
    notification: Notification,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .animateContentSize()
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (notification.isRead.not()) {
            DotBadge()
            Space(12.dp)
        }

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
                .size(28.dp),
        )

        Space(12.dp)

        Column(Modifier.weight(1f)) {
            Text(
                text = notification.message,
                modifier = Modifier.fillMaxWidth(),
                color = MdtTheme.color.onSurface,
                style = MdtTheme.typo.sm.normal.copy(lineHeight = 18.sp),
            )

            Space(4.dp)

            Text(
                text = MdtLocale.formatDuration(notification.createdAt),
                modifier = Modifier.fillMaxWidth(),
                color = MdtTheme.color.onSurfaceVariant,
                style = MdtTheme.typo.xs.normal,
            )
        }

        Space(12.dp)

        if (notification.link.isNotBlank()) {
            Icon(
                painter = MdtIcons.ExternalLink,
                contentDescription = null,
                tint = MdtTheme.color.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewEmpty() {
    PreviewTheme {
        ScreenContent(
            notifications = emptyList(),
            onNotificationClick = {},
            onInternalRouteClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewList() {
    PreviewTheme {
        ScreenContent(
            notifications = listOf(
                Notification(
                    id = "1",
                    category = Notification.Category.Updates,
                    link = "https://2fas.com",
                    internalRoute = null,
                    message = "A new version of the app is available. Update now to get the latest features.",
                    createdAt = 1_000_000_000_000,
                    isRead = false,
                ),
                Notification(
                    id = "2",
                    category = Notification.Category.News,
                    link = "",
                    internalRoute = null,
                    message = "2FAS is now available on more platforms. Check out what's new.",
                    createdAt = 900_000_000_000,
                    isRead = true,
                ),
                Notification(
                    id = "3",
                    category = Notification.Category.Features,
                    link = "",
                    internalRoute = null,
                    message = "You can now sync your tokens across devices securely.",
                    createdAt = 800_000_000_000,
                    isRead = true,
                ),
                Notification(
                    id = "4",
                    category = Notification.Category.Youtube,
                    link = "https://youtube.com",
                    internalRoute = null,
                    message = "Watch our latest tutorial on setting up two-factor authentication.",
                    createdAt = 700_000_000_000,
                    isRead = true,
                ),
                Notification(
                    id = "5",
                    category = Notification.Category.Tips,
                    link = "",
                    internalRoute = null,
                    message = "Tip: Enable a backup to never lose access to your accounts.",
                    createdAt = 600_000_000_000,
                    isRead = true,
                ),
            ),
            onNotificationClick = {},
            onInternalRouteClick = {},
        )
    }
}