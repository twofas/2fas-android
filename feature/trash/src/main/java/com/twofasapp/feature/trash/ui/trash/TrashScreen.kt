package com.twofasapp.feature.trash.ui.trash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.data.services.domain.Service
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwDropdownMenu
import com.twofasapp.designsystem.common.TwDropdownMenuItem
import com.twofasapp.designsystem.common.TwIconButton
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.service.ServiceImageType
import com.twofasapp.designsystem.service.ServiceNoCode
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun TrashRoute(
    openDispose: (Long) -> Unit,
    viewModel: TrashViewModel = koinViewModel()
) {
    val services by viewModel.services.collectAsStateWithLifecycle()

    TrashScreen(
        services = services,
        onRestoreClick = { viewModel.restoreService(it) },
        onDisposeClick = { openDispose(it) },
    )
}

@Composable
private fun TrashScreen(
    services: List<Service>,
    onRestoreClick: (Long) -> Unit,
    onDisposeClick: (Long) -> Unit,
) {

    Scaffold(topBar = { TwTopAppBar(TwLocale.strings.trashTitle) }) { padding ->

        LazyColumn(Modifier.padding(padding)) {

            if (services.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        Text(
                            text = TwLocale.strings.trashEmpty,
                            style = TwTheme.typo.body3,
                            color = TwTheme.color.onSurfacePrimary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                return@LazyColumn
            }

            items(services, key = { it.id }) {
                ServiceNoCode(
                    name = it.name,
                    info = it.info,
                    imageType = when (it.imageType) {
                        Service.ImageType.IconCollection -> ServiceImageType.Icon
                        Service.ImageType.Label -> ServiceImageType.Label
                    },
                    iconLight = it.iconLight,
                    iconDark = it.iconDark,
                    labelText = it.labelText,
                    labelColor = it.badgeColor.asState(),
                    imageSize = 32.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 0.dp),
                    endContent = {
                        var expanded by rememberSaveable { mutableStateOf(false) }

                        TwDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            anchor = { TwIconButton(painter = TwIcons.More, onClick = { expanded = true }) }
                        ) {
                            TwDropdownMenuItem(
                                text = TwLocale.strings.trashRestoreCta,
                                onClick = {
                                    expanded = false
                                    onRestoreClick(it.id)
                                }
                            )
                            TwDropdownMenuItem(
                                text = TwLocale.strings.trashDisposeCta,
                                onClick = {
                                    expanded = false
                                    onDisposeClick(it.id)
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun Service.Tint?.asState(): Color {
    return when (this) {
        Service.Tint.Default -> TwTheme.color.surfaceVariant
        Service.Tint.LightBlue -> TwTheme.color.accentLightBlue
        Service.Tint.Indigo -> TwTheme.color.accentIndigo
        Service.Tint.Purple -> TwTheme.color.accentPurple
        Service.Tint.Turquoise -> TwTheme.color.accentTurquoise
        Service.Tint.Green -> TwTheme.color.accentGreen
        Service.Tint.Red -> TwTheme.color.accentRed
        Service.Tint.Orange -> TwTheme.color.accentOrange
        Service.Tint.Yellow -> TwTheme.color.accentYellow
        null -> TwTheme.color.surfaceVariant
    }
}