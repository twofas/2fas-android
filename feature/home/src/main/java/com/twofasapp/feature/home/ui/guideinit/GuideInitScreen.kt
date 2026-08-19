package com.twofasapp.feature.home.ui.guideinit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.other.Divider
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.assetAsBitmap
import com.twofasapp.feature.home.ui.guides.Guide
import com.twofasapp.feature.home.ui.guides.GuideJson
import com.twofasapp.feature.home.ui.guides.getGuideJson
import com.twofasapp.feature.home.ui.guides.iconFile
import com.twofasapp.feature.home.ui.guides.json
import com.twofasapp.locale.MdtLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject

@Composable
internal fun GuideInitScreen(
    json: Json = koinInject(),
    guide: Guide,
    openGuide: (Guide, Int) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var guideJson by remember { mutableStateOf<GuideJson?>(null) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            json.decodeFromString<GuideJson>(context.getGuideJson(guide.json)).also { guideJson = it }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = MdtLocale.strings.guideTitle.format(guideJson?.serviceName)) },
    ) { padding ->

        guideJson?.let { guideJson ->
            Content(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                guide = guide,
                guideJson = guideJson,
                openGuide = openGuide,
            )
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    guide: Guide,
    guideJson: GuideJson,
    openGuide: (Guide, Int) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Image(
            bitmap = context.assetAsBitmap(guide.iconFile()).asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.size(60.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = guideJson.serviceName,
            style = MdtTheme.typo.regular.xl2,
            color = MdtTheme.color.onSurface,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = guideJson.flow.header,
            style = MaterialTheme.typography.bodyLarge,
            color = MdtTheme.color.onSurface,
            modifier = Modifier.padding(horizontal = 24.dp),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(1f))

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            text = guideJson.flow.menu.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MdtTheme.color.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))

        guideJson.flow.menu.items.forEachIndexed { index, menuItem ->
            Text(
                text = menuItem.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MdtTheme.color.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        openGuide(guide, index)
                    }
                    .padding(horizontal = 24.dp, vertical = 20.dp),
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    Content(
        modifier = Modifier
            .fillMaxSize()
            .background(MdtTheme.color.background),
        guide = Guide.Universal,
        guideJson = PreviewGuide,
        openGuide = { _, _ -> },
    )
}

internal val PreviewGuide: GuideJson = GuideJson(
    serviceName = "Universal 2FA Guide",
    serviceId = "",
    flow = GuideJson.Flow(
        header = "Follow our universal guide to pair your account with 2FAS app.",
        menu = GuideJson.Menu(
            title = "Please be aware that this is an overall guide.",
            items = listOf(
                GuideJson.Variant(name = "Desktop", steps = emptyList()),
                GuideJson.Variant(name = "Mobile", steps = emptyList()),
            ),
        ),
    ),
)