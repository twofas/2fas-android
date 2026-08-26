package com.twofasapp.feature.trash.ui.trash

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.DsServiceSimple
import com.twofasapp.core.design.feature.items.ServiceImageType
import com.twofasapp.core.design.feature.items.ServiceState
import com.twofasapp.core.design.feature.items.asColor
import com.twofasapp.core.design.foundation.button.IconButton
import com.twofasapp.core.design.foundation.menu.DropdownMenu
import com.twofasapp.core.design.foundation.menu.DropdownMenuItem
import com.twofasapp.core.design.foundation.screen.EmptyScreen
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.feature.trash.R
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun TrashScreen(
    viewModel: TrashViewModel = koinViewModel(),
    openDispose: (Long) -> Unit,
) {
    val services by viewModel.services.collectAsStateWithLifecycle()

    ScreenContent(
        services = services,
        onRestoreClick = { viewModel.restoreService(it) },
        onDisposeClick = { openDispose(it) },
    )
}

@Composable
private fun ScreenContent(
    services: List<Service>,
    onRestoreClick: (Long) -> Unit,
    onDisposeClick: (Long) -> Unit,
) {
    Scaffold(topBar = { TopAppBar(TwLocale.strings.trashTitle) }) { padding ->

        LazyColumn(Modifier.padding(padding)) {
            if (services.isEmpty()) {
                item {
                    EmptyScreen(
                        body = TwLocale.strings.trashEmpty,
                        image = painterResource(id = R.drawable.img_trash),
                        modifier = Modifier.fillParentMaxSize(),
                    )
                }

                return@LazyColumn
            }

            items(services, key = { it.id }) {
                DsServiceSimple(
                    state = ServiceState(
                        name = it.name,
                        info = it.info,
                        imageType = when (it.imageType) {
                            Service.ImageType.IconCollection -> ServiceImageType.Icon
                            Service.ImageType.Label -> ServiceImageType.Label
                        },
                        iconLight = it.iconLight,
                        iconDark = it.iconDark,
                        labelText = it.labelText,
                        labelColor = it.labelColor.asColor(),
                        revealed = true,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 0.dp),
                ) {
                    var dropdownVisible by rememberSaveable { mutableStateOf(false) }

                    DropdownMenu(
                        expanded = dropdownVisible,
                        onDismissRequest = { dropdownVisible = false },
                        anchor = { IconButton(painter = MdtIcons.More, onClick = { dropdownVisible = true }) },
                    ) {
                        DropdownMenuItem(
                            text = TwLocale.strings.trashRestoreCta,
                            icon = MdtIcons.Refresh,
                            onClick = {
                                dropdownVisible = false
                                onRestoreClick(it.id)
                            },
                        )
                        DropdownMenuItem(
                            text = TwLocale.strings.trashDisposeCta,
                            icon = MdtIcons.Delete,
                            contentColor = MdtTheme.color.accentRed,
                            onClick = {
                                dropdownVisible = false
                                onDisposeClick(it.id)
                            },
                        )
                    }
                }
            }
        }
    }
}