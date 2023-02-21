package com.twofasapp.notifications.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.twofasapp.base.BaseComponentActivity
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.design.theme.*
import com.twofasapp.extensions.openBrowserApp
import com.twofasapp.notifications.R
import com.twofasapp.notifications.domain.model.Notification
import com.twofasapp.time.domain.formatter.DurationFormatter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationsActivity : BaseComponentActivity() {

    private val viewModel: NotificationsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppThemeLegacy {
                Scaffold(
                    topBar = { Toolbar(title = stringResource(id = com.twofasapp.resources.R.string.commons__notifications)) { onBackPressed() } }
                ) { padding ->
                    NotificationList(padding)
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    it.handleEvent {
                        when (it) {
                            is NotificationsUiState.Event.OpenBrowser -> openBrowserApp(url = it.url)
                        }
                        viewModel.eventHandled(it.id)
                    }

                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun NotificationList(padding: PaddingValues) {
        val uiState = viewModel.uiState.collectAsState()

        if (uiState.value.items.isNotEmpty()) {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(uiState.value.items, key = { it.id }) {
                    NotificationItem(it, modifier = Modifier.animateItemPlacement())
                }
            }
        } else {
            Text(
                text = stringResource(id = com.twofasapp.resources.R.string.notifications__no_notifications),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                style = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.textSecondary, fontSize = 16.sp),
                textAlign = TextAlign.Center,
            )
        }
    }

    @Composable
    private fun NotificationItem(
        notification: Notification,
        modifier: Modifier = Modifier,
        durationFormatter: DurationFormatter = get(),
    ) {
        Column(
            modifier = modifier
                .animateContentSize()
                .background(if (notification.isRead) MaterialTheme.colors.backgroundSecondary else MaterialTheme.colors.background)
                .clickable { viewModel.itemClicked(notification) }
                .padding(16.dp)
        ) {

            Row {
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
                        .size(48.dp)
                        .padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = notification.message,
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        color = MaterialTheme.colors.textPrimary,
                        fontSize = 18.sp
                    ),
                )

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    modifier = Modifier
                        .size(28.dp)
                        .padding(top = 6.dp), onClick = { viewModel.itemClicked(notification) }
                ) {
                    Icon(painterResource(com.twofasapp.resources.R.drawable.ic_external_link), null, tint = MaterialTheme.colors.icon)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row {
                Spacer(modifier = Modifier.width(62.dp))
                Text(
                    text = durationFormatter.format(notification.publishTime),
                    style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.textSecondary),
                )
            }
        }
    }
}
