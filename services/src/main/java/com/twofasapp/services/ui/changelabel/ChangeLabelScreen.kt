package com.twofasapp.services.ui.changelabel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twofasapp.common.domain.Service
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwOutlinedTextField
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.ktx.LocalBackDispatcher
import com.twofasapp.designsystem.ktx.dpToSp
import com.twofasapp.designsystem.service.asColor
import com.twofasapp.locale.R
import com.twofasapp.services.ui.EditServiceViewModel

@Composable
internal fun ChangeLabelScreen(
    viewModel: EditServiceViewModel,
) {
    val service = viewModel.uiState.collectAsState().value.service
    val labelText = remember { mutableStateOf(service.labelText ?: service.name.take(2).uppercase()) }
    val labelTint = remember { mutableStateOf(service.labelColor ?: Service.Tint.Default) }
    val backDispatcher = LocalBackDispatcher

    Scaffold(
        topBar = {
            TwTopAppBar(titleText = stringResource(id = R.string.customization_edit_label), actions = {
                TextButton(onClick = { backDispatcher.onBackPressed() }) {
                    Text(text = stringResource(id = R.string.commons__done))
                }
            })
        }
    ) { padding ->

        Column(Modifier.padding(padding)) {
            Box(modifier = Modifier.padding(24.dp)) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(shape = CircleShape, color = service.labelColor.asColor())
                ) {
                    Box(
                        modifier = Modifier
                            .width(45.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(TwTheme.color.background)
                            .align(Alignment.Center),
                    )

                    Text(
                        text = labelText.value,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        style = TwTheme.typo.body3.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = dpToSp(dp = 22.dp),
                            lineHeight = dpToSp(dp = 32.dp)
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Box(modifier = Modifier.padding(24.dp)) {
                TwOutlinedTextField(
                    value = labelText.value,
                    labelText = stringResource(id = R.string.tokens__label_characters_title),
                    maxLength = 2,
                    onValueChange = {
                        labelText.value = it.uppercase()
                        viewModel.updateLabel(labelText.value.uppercase(), labelTint.value)
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters),
                )
            }

            LazyRow {
                items(Service.Tint.values().toList(), key = { it.name }) {
                    Column(modifier = Modifier
                        .width(80.dp)
                        .clip(CircleShape)
                        .clickable {
                            labelTint.value = it

                            viewModel.updateLabel(labelText.value.uppercase(), labelTint.value)
                        }
                        .padding(vertical = 12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.CenterHorizontally)
                                .clip(CircleShape)
                                .border(
                                    BorderStroke(if (it == labelTint.value) 50.dp else 5.dp, SolidColor(it.asColor())), CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            stringResource(
                                id = when (it) {
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
                                }
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(color = TwTheme.color.onSurfacePrimary),
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .align(Alignment.CenterHorizontally)
                                .wrapContentWidth(),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
