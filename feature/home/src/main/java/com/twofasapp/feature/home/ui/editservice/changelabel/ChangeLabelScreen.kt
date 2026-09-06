package com.twofasapp.feature.home.ui.editservice.changelabel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.ServiceImageType
import com.twofasapp.core.design.feature.items.asColor
import com.twofasapp.core.design.feature.items.servicecard.ServiceCardImage
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.textfield.TextField
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.LocalBackDispatcher
import com.twofasapp.feature.home.ui.editservice.EditServiceViewModel
import com.twofasapp.locale.R

@Composable
internal fun ChangeLabelScreen(
    viewModel: EditServiceViewModel,
) {
    val service = viewModel.uiState.collectAsState().value.service
    val backDispatcher = LocalBackDispatcher

    Content(
        service = service,
        onUpdateLabel = { text, tint -> viewModel.updateLabel(text, tint) },
        onDone = { backDispatcher.onBackPressed() },
    )
}

@Composable
private fun Content(
    service: Service,
    onUpdateLabel: (String, Service.Tint) -> Unit = { _, _ -> },
    onDone: () -> Unit = {},
) {
    var labelText by remember { mutableStateOf(service.labelText ?: service.name.take(2).uppercase()) }
    var labelTint by remember { mutableStateOf(service.labelColor ?: Service.Tint.Default) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.customization_edit_label),
                actions = {
                    Button(
                        text = stringResource(id = R.string.commons__done),
                        style = ButtonStyle.Text,
                        onClick = { onDone() },
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MdtTheme.color.background)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Space(24.dp)

            ServiceCardImage(
                type = ServiceImageType.Label,
                iconLight = "",
                iconDark = "",
                labelText = labelText,
                labelColor = labelTint.asColor(),
                modifier = Modifier.size(64.dp),
            )

            Space(48.dp)

            TextField(
                value = labelText,
                labelText = stringResource(id = R.string.tokens__label_characters_title),
                onValueChange = {
                    if (it.length <= 2) {
                        labelText = it.uppercase()
                        onUpdateLabel(labelText, labelTint)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Space(24.dp)

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) {
                items(Service.Tint.entries, key = { it.name }) { tint ->
                    Column(
                        modifier = Modifier
                            .width(80.dp)
                            .clip(CircleShape)
                            .clickable {
                                labelTint = tint
                                onUpdateLabel(labelText, labelTint)
                            }
                            .padding(vertical = 12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.CenterHorizontally)
                                .clip(CircleShape)
                                .border(
                                    BorderStroke(if (tint == labelTint) 50.dp else 5.dp, SolidColor(tint.asColor())),
                                    CircleShape,
                                ),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = tint.toLabel(),
                            style = MdtTheme.typo.xs2.normal,
                            color = MdtTheme.color.onSurface,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .align(Alignment.CenterHorizontally)
                                .wrapContentWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Service.Tint.toLabel(): String {
    return stringResource(
        id = when (this) {
            Service.Tint.Default -> R.string.color__neutral
            Service.Tint.LightBlue -> R.string.color__light_blue
            Service.Tint.Indigo -> R.string.color__indigo
            Service.Tint.Purple -> R.string.color__purple
            Service.Tint.Turquoise -> R.string.color__turquoise
            Service.Tint.Green -> R.string.color__green
            Service.Tint.Red -> R.string.color__red
            Service.Tint.Orange -> R.string.color__orange
            Service.Tint.Yellow -> R.string.color__yellow
            Service.Tint.Pink -> R.string.color__pink
            Service.Tint.Brown -> R.string.color__brown
        },
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            service = Service.Preview,
        )
    }
}