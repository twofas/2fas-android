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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.ResponsiveText
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.ktx.assetAsBitmap
import com.twofasapp.designsystem.ktx.openSafely
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun GuidesScreen(
    viewModel: GuidesViewModel = koinViewModel(),
    openGuide: (Guide) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GuidesScreenContent(
        uiState = uiState,
        onGuideClick = openGuide,
    )
}

@Composable
private fun GuidesScreenContent(
    uiState: GuidesUiState,
    onGuideClick: (Guide) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val guides by remember { mutableStateOf(Guide.entries) }

    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.guidesSelectTitle) }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = TwLocale.strings.guidesSelectDescription,
                style = TwTheme.typo.body1,
                modifier = Modifier.padding(16.dp),
                color = TwTheme.color.onSurfacePrimary,
            )

            guides.chunked(2).forEach { chunk ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
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

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TwTheme.color.surface)
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = TwLocale.strings.guidesSelectProvideGuide,
                    style = TwTheme.typo.body3,
                    color = TwTheme.color.onSurfacePrimary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { uriHandler.openSafely("https://2fas.com/y2g") },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = TwTheme.color.primary,
                        disabledContentColor = TwTheme.color.onSurfaceSecondary,
                    )
                ) {
                    Text(
                        text = TwLocale.strings.guidesSelectProvideGuideCta,
                        style = TwTheme.typo.body2,
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        painter = TwIcons.ExternalLink,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
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
            .border(2.dp, TwTheme.color.divider, RoundedCornerShape(20.dp))
            .clickable { onClick(guide) }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
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
                    .size(56.dp)
            )

            ResponsiveText(
                text = when (guide) {
                    Guide.Facebook -> "Facebook"
                    Guide.Twitter -> "Twitter"
                    Guide.Amazon -> "Amazon"
                    Guide.Universal -> "Universal Guide"
                },
                style = TwTheme.typo.body3,
                color = TwTheme.color.onSurfacePrimary,
                maxLines = 1,
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    GuidesScreenContent(
        uiState = GuidesUiState(),
        onGuideClick = {},
    )
}