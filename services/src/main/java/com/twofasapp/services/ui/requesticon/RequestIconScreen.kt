package com.twofasapp.services.ui.requesticon

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import com.twofasapp.core.analytics.AnalyticsEvent
import com.twofasapp.core.analytics.AnalyticsService
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.extensions.openBrowserApp
import com.twofasapp.resources.R
import org.koin.androidx.compose.get

@Composable
internal fun RequestIconScreen(
    analyticsService: AnalyticsService = get(),
) {
    val activity = LocalContext.current as? Activity
    val shareText =
        stringResource(id = R.string.tokens__request_icon_provider_message)

    Scaffold(
        topBar = { TwTopAppBar(titleText = stringResource(id = R.string.customization_request_icon)) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_message),
                contentDescription = null,
                modifier = Modifier
                    .height(64.dp)
                    .align(CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.tokens__request_icon_social_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier
                .align(CenterHorizontally)
                .clickable {
                    analyticsService.captureEvent(AnalyticsEvent.REQUEST_ICON_DISCORD_CLICK)
                    activity?.openBrowserApp(url = "https://discord.gg/q4cP6qh2g5")
                }) {
                Text(
                    text = stringResource(id = R.string.tokens__request_icon_social_link),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TwTheme.color.primary,
                    modifier = Modifier.align(CenterVertically)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_open_new),
                    contentDescription = null,
                    tint = TwTheme.color.primary,
                    modifier = Modifier
                        .size(20.dp)
                        .align(CenterVertically)
                )
            }
            Text(
                text = stringResource(id = R.string.tokens__request_icon_social_description),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )


            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Center)
                )
                Text(
                    text = stringResource(id = R.string.tokens__request_icon_middle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TwTheme.color.onSurfaceSecondary,
                    modifier = Modifier
                        .align(Center)
                        .background(color = TwTheme.color.background)
                        .padding(horizontal = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))


            Image(
                painter = painterResource(id = R.drawable.image_icon_request),
                contentDescription = null,
                modifier = Modifier
                    .height(64.dp)
                    .align(CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.tokens__request_icon_provider_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.tokens__request_icon_provider_description),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            SelectionContainer {
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                        .padding(24.dp)
                        .border(width = 1.dp, color = TwTheme.color.divider, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 4.dp)
                ) {

                    Text(
                        text = shareText,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .weight(1f)
                            .padding(12.dp)
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp),
                        color = TwTheme.color.divider
                    )
                    IconButton(modifier = Modifier.align(CenterVertically),
                        onClick = {
                            analyticsService.captureEvent(AnalyticsEvent.REQUEST_ICON_SHARE_CLICK)
                            ShareCompat.IntentBuilder(activity!!)
                                .setType("text/plain")
                                .setChooserTitle("2FAS Icon Request")
                                .setText(shareText)
                                .startChooser()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            tint = TwTheme.color.primary,
                            contentDescription = null
                        )
                    }
                }
            }

            Text(
                text = stringResource(id = R.string.tokens__request_icon_provider_footnote),
                style = MaterialTheme.typography.bodySmall,
                color = TwTheme.color.onSurfacePrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
