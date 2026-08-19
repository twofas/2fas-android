package com.twofasapp.feature.home.ui.editservice.requesticon

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
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.locale.R

@Composable
internal fun RequestIconScreen() {
    val activity = LocalContext.current as? Activity
    val shareText =
        stringResource(id = R.string.tokens__request_icon_provider_message)

    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = { TopAppBar(title = stringResource(id = R.string.customization_request_icon)) },
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = com.twofasapp.core.design.R.drawable.img_order_icon_message),
                contentDescription = null,
                modifier = Modifier
                    .height(64.dp)
                    .align(CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.tokens__request_icon_social_title),
                style = MaterialTheme.typography.titleMedium,
                color = MdtTheme.color.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .clickable {
                        uriHandler.openUri("https://discord.gg/q4cP6qh2g5")
                    },
            ) {
                Text(
                    text = stringResource(id = R.string.tokens__request_icon_social_link),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MdtTheme.color.primary,
                    modifier = Modifier.align(CenterVertically),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = MdtIcons.ExternalLink,
                    contentDescription = null,
                    tint = MdtTheme.color.primary,
                    modifier = Modifier
                        .size(20.dp)
                        .align(CenterVertically),
                )
            }
            Text(
                text = stringResource(id = R.string.tokens__request_icon_social_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MdtTheme.color.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Center),
                )
                Text(
                    text = stringResource(id = R.string.tokens__request_icon_middle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MdtTheme.color.onSurfaceVariant,
                    modifier = Modifier
                        .align(Center)
                        .background(color = MdtTheme.color.background)
                        .padding(horizontal = 16.dp),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = com.twofasapp.core.design.R.drawable.img_order_icon_share),
                contentDescription = null,
                modifier = Modifier
                    .height(64.dp)
                    .align(CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.tokens__request_icon_provider_title),
                style = MaterialTheme.typography.titleMedium,
                color = MdtTheme.color.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.tokens__request_icon_provider_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MdtTheme.color.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            SelectionContainer {
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                        .padding(24.dp)
                        .border(width = 1.dp, color = MdtTheme.color.divider, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 4.dp),
                ) {
                    Text(
                        text = shareText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MdtTheme.color.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .padding(12.dp),
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp),
                        color = MdtTheme.color.divider,
                    )
                    IconButton(
                        modifier = Modifier.align(CenterVertically),
                        onClick = {
                            ShareCompat.IntentBuilder(activity!!)
                                .setType("text/plain")
                                .setChooserTitle("2FAS Icon Request")
                                .setText(shareText)
                                .startChooser()
                        },
                    ) {
                        Icon(
                            painter = MdtIcons.Share,
                            tint = MdtTheme.color.primary,
                            contentDescription = null,
                        )
                    }
                }
            }

            Text(
                text = stringResource(id = R.string.tokens__request_icon_provider_footnote),
                style = MaterialTheme.typography.bodySmall,
                color = MdtTheme.color.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}