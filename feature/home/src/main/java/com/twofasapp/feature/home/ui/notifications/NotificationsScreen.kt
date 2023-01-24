package com.twofasapp.feature.home.ui.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.data.notifications.domain.Notification
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.feature.home.R
import com.twofasapp.feature.home.ui.bottombar.BottomBar
import com.twofasapp.feature.home.ui.bottombar.BottomBarListener
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun NotificationsRoute(
    bottomBarListener: BottomBarListener,
    viewModel: NotificationsViewModel = koinViewModel(),
) {
    val notifications by viewModel.notificationsList.collectAsStateWithLifecycle()

    NotificationsScreen(
        bottomBarListener = bottomBarListener,
        notifications = notifications,
        onNotificationClick = { viewModel.onNotificationClick(it) }
    )
}

@Composable
private fun NotificationsScreen(
    bottomBarListener: BottomBarListener,
    notifications: List<Notification>,
    onNotificationClick: (Notification) -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        bottomBar = { BottomBar(2, bottomBarListener) },
        topBar = { TwTopAppBar(titleText = TwLocale.strings.notificationsTitle, showBackButton = false) },
    ) { padding ->

        LazyColumn(Modifier.padding(padding)) {

            if (notifications.isEmpty()) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = TwLocale.strings.notificationsEmpty,
                            style = TwTheme.typo.body3,
                            color = TwTheme.color.onSurfacePrimary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                return@LazyColumn
            }

            items(notifications, key = { it.id }) { notification ->
                Notification(
                    notification = notification,
                    modifier = Modifier
                        .clickable {
                            onNotificationClick(notification)
                            uriHandler.openUri(notification.link)
                        }
                        .background(if (notification.isRead) TwTheme.color.surface else TwTheme.color.background)
                        .padding(16.dp)
                )
                Divider(color = TwTheme.color.divider)
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
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = notification.message,
                modifier = Modifier.fillMaxWidth(),
                color = TwTheme.color.onSurfacePrimary,
                style = TwTheme.typo.body3
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = TwLocale.formatDuration(notification.publishTime),
                modifier = Modifier.fillMaxWidth(),
                color = TwTheme.color.onSurfaceSecondary,
                style = TwTheme.typo.body4
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        Icon(painter = TwIcons.ExternalLink, contentDescription = null, tint = TwTheme.color.iconTint)
    }
}