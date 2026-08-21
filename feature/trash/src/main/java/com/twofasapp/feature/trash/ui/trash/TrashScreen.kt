package com.twofasapp.feature.trash.ui.trash

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.DsServiceSimple
import com.twofasapp.core.design.feature.items.ServiceImageType
import com.twofasapp.core.design.feature.items.ServiceState
import com.twofasapp.core.design.feature.items.asColor
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.IconButton
import com.twofasapp.core.design.foundation.checked.CheckIcon
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.layout.ActionsRow
import com.twofasapp.core.design.foundation.screen.LazyContent
import com.twofasapp.core.design.foundation.text.TextIcon
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.toastShort
import com.twofasapp.core.design.state.ScreenState
import com.twofasapp.core.design.theme.RoundedTopShape
import com.twofasapp.core.design.theme.ScreenPadding
import com.twofasapp.locale.MdtLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun TrashScreen(
    viewModel: TrashViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Content(
        uiState = uiState,
        screenState = screenState,
        onItemToggled = { viewModel.toggle(it) },
        onSelectAll = { viewModel.selectAll() },
        onClearSelections = { viewModel.clearSelections() },
        onRestoreClick = {
            viewModel.restore {
                context.toastShort(it)
            }
        },
        onDeleteConfirmed = {
            viewModel.delete {
                context.toastShort(it)
            }
        },
    )
}

@Composable
private fun Content(
    uiState: TrashUiState,
    screenState: ScreenState,
    onItemToggled: (Service) -> Unit = {},
    onSelectAll: () -> Unit = {},
    onClearSelections: () -> Unit = {},
    onRestoreClick: () -> Unit = {},
    onDeleteConfirmed: () -> Unit = {},
) {
    val strings = MdtLocale.strings
    val onBackDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = if (uiState.hasSelections) {
                    strings.trashSelectedItems.format(uiState.selected.size)
                } else {
                    strings.trashTitle
                },
                navigationIcon = {
                    IconButton(
                        icon = if (uiState.hasSelections) MdtIcons.Close else MdtIcons.ArrowBack,
                        modifier = Modifier.testTag("trashNavigationButton"),
                        onClick = {
                            if (uiState.hasSelections) {
                                onClearSelections()
                            } else {
                                onBackDispatcher?.onBackPressed()
                            }
                        },
                    )
                },
                actions = {
                    if (uiState.trashedItems.isNotEmpty()) {
                        ActionsRow {
                            IconButton(
                                icon = MdtIcons.CheckAll,
                                modifier = Modifier.testTag("trashSelectAllButton"),
                                onClick = onSelectAll,
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyContent(
                screenState = screenState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MdtTheme.color.background),
                contentPadding = PaddingValues(bottom = if (uiState.selected.isEmpty()) 0.dp else 2 * ScreenPadding + 40.dp),
                itemsWhenSuccess = {
                    uiState.trashedItems.forEach { item ->
                        item(key = item.id, contentType = "Item") {
                            TrashItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem(fadeInSpec = null, fadeOutSpec = null),
                                service = item,
                                checked = uiState.selected.contains(item.id),
                                onCheckedChange = { onItemToggled(item) },
                            )
                        }
                    }
                },
                emptyIcon = MdtIcons.Delete,
            )

            AnimatedVisibility(
                visible = uiState.hasSelections && screenState.loading.not(),
                enter = slideInVertically(initialOffsetY = { it / 2 }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MdtTheme.color.surfaceContainer, RoundedTopShape)
                        .padding(ScreenPadding)
                        .padding(bottom = padding.calculateBottomPadding()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        height = 40.dp,
                        onClick = onRestoreClick,
                        content = {
                            TextIcon(
                                text = strings.trashRestoreCta,
                                leadingIcon = MdtIcons.Refresh,
                                leadingIconTint = MdtTheme.color.onPrimary,
                            )
                        },
                    )

                    Button(
                        modifier = Modifier.weight(1f),
                        height = 40.dp,
                        onClick = { showDeleteDialog = true },
                        content = {
                            TextIcon(
                                text = strings.trashDisposeCta,
                                leadingIcon = MdtIcons.Delete,
                                leadingIconTint = MdtTheme.color.onPrimary,
                            )
                        },
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        // TODO: Replace with a Modal
        ConfirmDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = "Confirm delete?",
            body = "TODO",
            icon = MdtIcons.Delete,
            onPositive = {
                onDeleteConfirmed()
            },
        )
    }
}

@Composable
private fun TrashItem(
    service: Service,
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: () -> Unit = {},
) {
    DsServiceSimple(
        state = ServiceState(
            name = service.name,
            info = service.info,
            imageType = when (service.imageType) {
                Service.ImageType.IconCollection -> ServiceImageType.Icon
                Service.ImageType.Label -> ServiceImageType.Label
            },
            iconLight = service.iconLight,
            iconDark = service.iconDark,
            labelText = service.labelText,
            labelColor = service.labelColor.asColor(),
            revealed = true,
        ),
        modifier = modifier
            .clickable { onCheckedChange() }
            .padding(start = 16.dp, end = 16.dp),
    ) {
        CheckIcon(checked = checked)
    }
}