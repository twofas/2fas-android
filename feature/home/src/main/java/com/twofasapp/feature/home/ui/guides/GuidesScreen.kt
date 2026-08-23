package com.twofasapp.feature.home.ui.guides

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.text.ResponsiveText
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.assetAsBitmap
import com.twofasapp.core.design.ktx.openSafely
import com.twofasapp.locale.MdtLocale
import org.koin.compose.koinInject

@Composable
internal fun GuidesScreen(
    navigator: Navigator = koinInject(),
) {
    GuidesScreenContent(
        onGuideClick = { navigator.open(Screen.GuideInit(guide = it.name)) },
    )
}

@Composable
private fun GuidesScreenContent(
    onGuideClick: (Guide) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val guides by remember { mutableStateOf(Guide.entries) }

    Scaffold(
        topBar = { TopAppBar(title = MdtLocale.strings.guidesSelectTitle) },
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding()),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = MdtLocale.strings.guidesSelectDescription,
                    style = MdtTheme.typo.base.normal,
                    modifier = Modifier.padding(16.dp),
                    color = MdtTheme.color.onSurface,
                )

                guides.chunked(2).forEach { chunk ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        if (chunk.firstOrNull() != null) {
                            GuideItem(guide = chunk.first(), modifier = Modifier.weight(1f), onClick = onGuideClick)
                        }

                        if (chunk.getOrNull(1) != null) {
                            GuideItem(guide = chunk[1], modifier = Modifier.weight(1f), onClick = onGuideClick)
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MdtTheme.color.surface)
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp, bottom = padding.calculateBottomPadding()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = MdtLocale.strings.guidesSelectProvideGuide,
                    style = MdtTheme.typo.sm.normal,
                    color = MdtTheme.color.onSurface,
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { uriHandler.openSafely("https://2fas.com/y2g") },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MdtTheme.color.primary,
                        disabledContentColor = MdtTheme.color.onSurfaceVariant,
                    ),
                ) {
                    Text(
                        text = MdtLocale.strings.guidesSelectProvideGuideCta,
                        style = MdtTheme.typo.sm.medium,
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        painter = MdtIcons.ExternalLink,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun GuideItem(
    modifier: Modifier = Modifier,
    guide: Guide,
    onClick: (Guide) -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, MdtTheme.color.divider, RoundedCornerShape(20.dp))
            .clickable { onClick(guide) }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            Image(
                bitmap = context.assetAsBitmap(guide.iconFile()).asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(56.dp),
            )

            ResponsiveText(
                text = when (guide) {
                    Guide.Facebook -> "Facebook"
                    Guide.Twitter -> "X"
                    Guide.Amazon -> "Amazon"
                    Guide.Universal -> "Universal Guide"
                    Guide.LinkedIn -> "LinkedIn"
                    Guide.EpicGames -> "Epic Games"
                    Guide.RockstarGames -> "Rockstar Games"
                    Guide.Discord -> "Discord"
                    Guide.Google -> "Google"
                    Guide.Instagram -> "Instagram"
                    Guide.PayPal -> "PayPal"
                    Guide.Reddit -> "Reddit"
                },
                style = MdtTheme.typo.sm.normal,
                color = MdtTheme.color.onSurface,
                maxLines = 1,
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    GuidesScreenContent {}
}