package com.twofasapp.feature.home.ui.services

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.ModalBottomSheet
import com.twofasapp.designsystem.common.isScrollingUp
import com.twofasapp.designsystem.ktx.copyToClipboard
import com.twofasapp.designsystem.ktx.currentActivity
import com.twofasapp.designsystem.lazy.listItem
import com.twofasapp.designsystem.lazy.listItems
import com.twofasapp.designsystem.service.Service
import com.twofasapp.designsystem.service.ServiceStyle
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.ui.bottombar.BottomBar
import com.twofasapp.feature.home.ui.bottombar.BottomBarListener
import com.twofasapp.feature.home.ui.services.modal.AddServiceModal
import com.twofasapp.feature.home.ui.services.modal.FocusServiceModal
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ServicesRoute(
    listener: HomeNavigationListener,
    bottomBarListener: BottomBarListener,
    onExternalImportClick: () -> Unit = {},
    viewModel: ServicesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ServicesScreen(
        uiState = uiState,
        listener = listener,
        bottomBarListener = bottomBarListener,
        onEventConsumed = { viewModel.consumeEvent(it) },
        onFabClick = { viewModel.toggleAddMenu() },
        onExternalImportClick = onExternalImportClick,
        onEditModeChange = { viewModel.toggleEditMode() },
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun ServicesScreen(
    uiState: ServicesUiState,
    listener: HomeNavigationListener,
    bottomBarListener: BottomBarListener,
    onEventConsumed: (ServicesStateEvent) -> Unit,
    onFabClick: () -> Unit = {},
    onExternalImportClick: () -> Unit = {},
    onEditModeChange: () -> Unit = {},
) {

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
    val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    var modalType by remember { mutableStateOf<ModalType>(ModalType.AddService) }
    val activity = LocalContext.currentActivity
    val scope = rememberCoroutineScope()
    val reorderableState = rememberReorderableLazyListState(
        maxScrollPerFrame = 40.dp,
        onMove = { from, to ->
//                viewModel.orderList.update {
//                    it.toMutableList().apply {
//                        add(to.index - 1, removeAt(from.index - 1))
//                    }
//                }
        }
    )

    uiState.events.firstOrNull()?.let {
        when (it) {
            ServicesStateEvent.ShowAddServiceModal -> {
                modalType = ModalType.AddService
                scope.launch { modalState.show() }
            }
        }
        onEventConsumed(it)
    }

    BackHandler(
        enabled = uiState.isInEditMode || modalState.isVisible
    ) {
        when {
            modalState.isVisible -> scope.launch { modalState.hide() }
            uiState.isInEditMode -> onEditModeChange()
        }
    }

    ModalBottomSheet(
        sheetState = modalState,
        sheetContent = {
            when (modalType) {
                is ModalType.AddService ->
                    AddServiceModal(
                        onAddManuallyClick = {
                            listener.openAddManuallyService(activity)
                            scope.launch { modalState.hide() }
                        },
                        onScanQrClick = {
                            listener.openAddQrService(activity)
                            scope.launch { modalState.hide() }
                        }
                    )

                is ModalType.FocusService -> {
                    val id = (modalType as ModalType.FocusService).id
                    uiState.getService(id)?.asState()?.let {
                        FocusServiceModal(
                            serviceState = it,
                            onEditClick = {
                                scope.launch { modalState.hide() }
                                listener.openService(activity, (modalType as ModalType.FocusService).id)
                            },
                            onCopyClick = {
                                scope.launch { modalState.hide() }
                                activity.copyToClipboard(
                                    uiState.getService(id)?.code?.current.toString()
                                )
                            }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = { BottomBar(0, bottomBarListener) },
            topBar = {
                ServicesAppBar(
                    isInEditMode = uiState.isInEditMode,
                    onEditModeChange = onEditModeChange,
                    scrollBehavior = scrollBehavior,
                )
            },
            floatingActionButton = {
                ServicesFab(
                    isVisible = uiState.isLoading.not(),
                    isExtendedVisible = uiState.services.isEmpty(),
                    isNormalVisible = reorderableState.listState.isScrollingUp(),
                    onClick = onFabClick,
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TwTheme.color.background)
                    .padding(padding)
                    .reorderable(reorderableState),
                state = reorderableState.listState,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                if (uiState.isLoading) {
                    listItem(ServicesListItem.Loader) {
                        ServicesProgress(
                            Modifier
                                .fillParentMaxSize()
                                .animateItemPlacement()
                        )
                    }
                    return@LazyColumn
                }

                if (uiState.services.isEmpty()) {
                    listItem(ServicesListItem.Empty) {
                        ServicesEmpty(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .animateItemPlacement(),
                            onExternalImportClick = onExternalImportClick
                        )
                    }
                }

                listItems(
                    items = uiState.services,
                    type = { ServicesListItem.Service(it.id) }
                ) { service ->
                    Divider(color = TwTheme.color.divider)

                    ReorderableItem(
                        state = reorderableState,
                        key = service.id,
                        modifier = Modifier
                            .animateItemPlacement()
                            .animateContentSize()
                    ) { isDragging ->
                        Service(
                            state = service.asState(),
                            style = if (uiState.isInEditMode) ServiceStyle.Edit else ServiceStyle.Default,
                            modifier = Modifier
                                .shadow(if (isDragging) 8.dp else 0.dp)
                                .detectReorderAfterLongPress(reorderableState),

//                                .run {
//                                    if (uiState.isInEditMode) {
//                                        detectReorderAfterLongPress(reorderableState)
//                                    } else {
//                                        this
//                                    }
//                                },
                            onClick = {
                                modalType = ModalType.FocusService(service.id)
                                scope.launch { modalState.show() }
                            },
                            onLongClick = {
                                activity.copyToClipboard(
                                    service.code?.current.toString()
                                )
                            },
                        )
                    }
                }

                item { Divider(color = TwTheme.color.divider) }
            }
        }
    }
}
