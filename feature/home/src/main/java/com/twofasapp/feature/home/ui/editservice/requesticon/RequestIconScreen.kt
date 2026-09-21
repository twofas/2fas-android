package com.twofasapp.feature.home.ui.editservice.requesticon

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonHeight
import com.twofasapp.core.design.foundation.button.IconButton
import com.twofasapp.core.design.foundation.icon.Icon
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.outline.HorizontalLine
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.theme.RoundedShape12
import com.twofasapp.locale.R

@Composable
internal fun RequestIconScreen() {
    val activity = LocalContext.current as? Activity
    val shareText = stringResource(id = R.string.tokens__request_icon_provider_message)
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = { TopAppBar(title = stringResource(id = R.string.customization_request_icon)) },
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(MdtTheme.color.background)
                .padding(padding),
            horizontalAlignment = CenterHorizontally,
        ) {
            Space(24.dp)

            Image(
                painter = painterResource(id = com.twofasapp.core.design.R.drawable.ic_discord),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )

            Space(16.dp)

            Text(
                text = stringResource(id = R.string.tokens__request_icon_social_title) + " " + stringResource(id = R.string.tokens__request_icon_social_description),
                style = MdtTheme.typo.base.medium,
                color = MdtTheme.color.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Space(16.dp)

            Button(
                text = stringResource(id = R.string.tokens__request_icon_social_link),
                onClick = { uriHandler.openUri("https://discord.gg/q4cP6qh2g5") },
                leadingIcon = MdtIcons.ExternalLink,
                size = ButtonHeight.Small,
                modifier = Modifier.align(CenterHorizontally),
            )

            Space(32.dp)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,

            ) {
                HorizontalLine(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )

                Text(
                    text = stringResource(id = R.string.tokens__request_icon_middle),
                    style = MdtTheme.typo.sm.normal,
                    color = MdtTheme.color.onSurfaceVariant,
                    modifier = Modifier
                        .align(Center)
                        .background(color = MdtTheme.color.background)
                        .padding(horizontal = 16.dp),
                )
            }

            Space(32.dp)

            Icon(
                painter = painterResource(id = com.twofasapp.core.design.R.drawable.ic_share),
                tint = MdtTheme.color.primary,
                modifier = Modifier.size(48.dp),
            )

            Space(16.dp)

            Text(
                text = stringResource(id = R.string.tokens__request_icon_provider_title),
                style = MdtTheme.typo.base.medium,
                color = MdtTheme.color.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Space(8.dp)

            Text(
                text = stringResource(id = R.string.tokens__request_icon_provider_description),
                style = MdtTheme.typo.sm.normal,
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
                        .padding(16.dp)
                        .border(width = 1.dp, color = MdtTheme.color.divider, shape = RoundedShape12)
                        .padding(horizontal = 4.dp),
                ) {
                    Text(
                        text = shareText,
                        style = MdtTheme.typo.xs2.normal,
                        color = MdtTheme.color.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .padding(12.dp),
                    )

                    IconButton(
                        icon = MdtIcons.Share,
                        iconTint = MdtTheme.color.primary,
                        modifier = Modifier.align(CenterVertically),
                        onClick = {
                            activity?.let {
                                ShareCompat.IntentBuilder(it)
                                    .setType("text/plain")
                                    .setChooserTitle("2FAS Icon Request")
                                    .setText(shareText)
                                    .startChooser()
                            }
                        },
                    )
                }
            }

            Text(
                text = stringResource(id = R.string.tokens__request_icon_provider_footnote),
                style = MdtTheme.typo.xs2.normal,
                color = MdtTheme.color.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Space(24.dp)
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewTheme {
        RequestIconScreen()
    }
}