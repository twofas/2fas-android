package com.twofasapp.feature.home.ui.services

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.domain.Service
import com.twofasapp.data.session.domain.ServicesSort
import com.twofasapp.data.session.domain.ServicesStyle
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.ModalBottomSheet
import com.twofasapp.designsystem.common.RequestPermission
import com.twofasapp.designsystem.common.TwEmptyScreen
import com.twofasapp.designsystem.common.TwOutlinedButton
import com.twofasapp.designsystem.common.isScrollingUp
import com.twofasapp.designsystem.dialog.ConfirmDialog
import com.twofasapp.designsystem.dialog.InputDialog
import com.twofasapp.designsystem.dialog.ListRadioDialog
import com.twofasapp.designsystem.group.ServicesGroup
import com.twofasapp.designsystem.ktx.currentActivity
import com.twofasapp.designsystem.lazy.listItem
import com.twofasapp.designsystem.service.DsService
import com.twofasapp.designsystem.service.ServiceStyle
import com.twofasapp.feature.home.R
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.ui.bottombar.BottomBar
import com.twofasapp.feature.home.ui.bottombar.BottomBarListener
import com.twofasapp.feature.home.ui.services.component.ServicesAppBar
import com.twofasapp.feature.home.ui.services.component.ServicesFab
import com.twofasapp.feature.home.ui.services.component.ServicesProgress
import com.twofasapp.feature.home.ui.services.component.SyncNoticeBar
import com.twofasapp.feature.home.ui.services.component.SyncReminder
import com.twofasapp.feature.home.ui.services.modal.AddServiceModal
import com.twofasapp.feature.home.ui.services.modal.FocusServiceModal
import com.twofasapp.feature.home.ui.services.modal.ModalType
import com.twofasapp.locale.TwLocale
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ServicesRoute(
    listener: HomeNavigationListener,
    bottomBarListener: BottomBarListener,
    viewModel: ServicesViewModel = koinViewModel()
) {
    val activity = LocalContext.currentActivity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ServicesScreen(
        uiState = uiState,
        listener = listener,
        bottomBarListener = bottomBarListener,
        onEventConsumed = { viewModel.consumeEvent(it) },
        onFabClick = { viewModel.toggleAddMenu() },
        onExternalImportClick = { listener.openExternalImport() },
        onEditModeChange = { viewModel.toggleEditMode() },
        onToggleGroupExpand = { viewModel.toggleGroup(it) },
        onAddGroup = { viewModel.addGroup(it) },
        onMoveUpGroup = { viewModel.moveUpGroup(it) },
        onMoveDownGroup = { viewModel.moveDownGroup(it) },
        onEditGroup = { id, name -> viewModel.editGroup(id, name) },
        onDeleteGroup = { viewModel.deleteGroup(it) },
        onDragStart = { viewModel.onDragStart() },
        onDragEnd = { viewModel.onDragEnd(it) },
        onSortChange = { viewModel.updateSort(it) },
        onSearchQueryChange = { viewModel.search(it) },
        onSearchFocusChange = { viewModel.searchFocused(it) },
        onOpenBackupClick = { listener.openBackup(activity) },
        onDismissSyncReminderClick = { viewModel.dismissSyncReminder() },
        onIncrementHotpCounterClick = { viewModel.incrementHotpCounter(it) },
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun ServicesScreen(
    uiState: ServicesUiState,
    listener: HomeNavigationListener,
    bottomBarListener: BottomBarListener,
    onEventConsumed: (ServicesStateEvent) -> Unit,
    onFabClick: () -> Unit = {},
    onExternalImportClick: () -> Unit = {},
    onEditModeChange: () -> Unit = {},
    onToggleGroupExpand: (String?) -> Unit = {},
    onAddGroup: (String) -> Unit = {},
    onMoveUpGroup: (String) -> Unit = {},
    onMoveDownGroup: (String) -> Unit = {},
    onEditGroup: (String, String) -> Unit = { _, _ -> },
    onDeleteGroup: (String) -> Unit = {},
    onDragStart: () -> Unit = { },
    onDragEnd: (List<ServicesListItem>) -> Unit = { },
    onSortChange: (Int) -> Unit = {},
    onSearchQueryChange: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    onOpenBackupClick: () -> Unit = {},
    onDismissSyncReminderClick: () -> Unit = {},
    onIncrementHotpCounterClick: (Service) -> Unit = {},
) {

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var showAddGroupDialog by remember { mutableStateOf(false) }
    var showEditGroupDialog by remember { mutableStateOf(false) }
    var showDeleteGroupDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showQrFromGalleryDialog by remember { mutableStateOf(false) }
    var askForPermission by remember { mutableStateOf(false) }
    var clickedGroup by remember { mutableStateOf<Group?>(null) }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    var modalType by remember { mutableStateOf<ModalType>(ModalType.AddService) }
    val activity = LocalContext.currentActivity
    val scope = rememberCoroutineScope()

    var isDragging by remember { mutableStateOf(false) }
//    val data = remember { mutableStateOf(List(100) { "Item $it" }) }
    val reorderableData = remember { mutableStateOf(uiState.items) }
    val listState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(
        listState = listState,
        onMove = { from, to ->
            onDragStart()
            isDragging = true
            val fromItem = reorderableData.value[from.index]
            val toItem = reorderableData.value[to.index]
            if (fromItem is ServicesListItem.ServiceItem) {
                if (toItem is ServicesListItem.ServiceItem || (toItem is ServicesListItem.GroupItem && toItem.group.id != null)) {
                    reorderableData.value = reorderableData.value.toMutableList().apply {
                        add(to.index, removeAt(from.index))
                    }
                }
            }
        },
        onDragEnd = { _, _ ->
            isDragging = false
            onDragEnd(reorderableData.value)
        },
    )

    val serviceContainerColor = if (uiState.totalGroups == 1) {
        TwTheme.color.background
    } else {
        TwTheme.color.serviceBackgroundWithGroups
    }
    val serviceContainerColorBlink = TwTheme.color.primary.copy(alpha = 0.2f)
    val serviceContainerColorBlinking = remember { Animatable(serviceContainerColor) }

    var recentlyAddedService by remember { mutableStateOf<Long?>(null) }
    reorderableData.value = uiState.items

    uiState.events.firstOrNull()?.let {
        when (it) {
            ServicesStateEvent.ShowAddServiceModal -> {
                modalType = ModalType.AddService
                scope.launch { modalState.show() }
            }

            ServicesStateEvent.ShowQrFromGalleryDialog -> {
                showQrFromGalleryDialog = true
            }

            is ServicesStateEvent.ShowServiceAddedModal -> {
                modalType = ModalType.FocusService(it.id, true)
                scope.launch { modalState.show() }
            }
        }
        onEventConsumed(it)
    }

    LaunchedEffect(Unit) {
        if (uiState.searchFocused) {
            focusRequester.requestFocus()
        }
    }

    BackHandler(
        enabled = uiState.isInEditMode || modalState.isVisible || uiState.searchFocused
    ) {
        when {
            modalState.isVisible -> scope.launch { modalState.hide() }
            uiState.isInEditMode -> onEditModeChange()
            uiState.searchQuery.isNotEmpty() -> onSearchQueryChange("")
            uiState.searchFocused -> onSearchFocusChange(false)
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            if (modalType is ModalType.FocusService && (modalType as ModalType.FocusService).isRecentlyAdded) {
                val id = (modalType as ModalType.FocusService).id

                scope.launch {
                    listState.animateScrollToItem(
                        uiState.items.indexOfFirst {
                            it is ServicesListItem.ServiceItem && it.service.id == id
                        }
                    )
                    recentlyAddedService = id

                    serviceContainerColorBlinking.animateTo(serviceContainerColorBlink, tween(0))
                    serviceContainerColorBlinking.animateTo(serviceContainerColor, tween(1500, easing = EaseOut))

                    recentlyAddedService = null
                }
            }
        },
        sheetState = modalState,
        sheetContent = {
            when (modalType) {
                is ModalType.AddService ->
                    AddServiceModal(
                        onAddManuallyClick = {
                            scope.launch {
                                modalState.hide()
                                listener.openAddManuallyService(activity)
                            }
                        },
                        onScanQrClick = {
                            scope.launch {
                                modalState.hide()
                                askForPermission = true
                            }
                        }
                    )

                is ModalType.FocusService -> {
                    val id = (modalType as ModalType.FocusService).id
                    val service = uiState.getService(id)
                    service?.asState()?.let {
                        FocusServiceModal(
                            serviceState = it,
                            showNextCode = uiState.appSettings.showNextCode,
                            onEditClick = {
                                scope.launch {
                                    modalState.hide()
                                    listener.openService(activity, (modalType as ModalType.FocusService).id)
                                }
                            },
                            onCopyClick = {
                                it.copyToClipboard(activity, uiState.appSettings.showNextCode)
                            },
                            onIncrementCounterClick = {
                                onIncrementHotpCounterClick(service)
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
                    query = uiState.searchQuery,
                    isInEditMode = uiState.isInEditMode,
                    isSearchFocused = uiState.searchFocused,
                    onEditModeChange = onEditModeChange,
                    scrollBehavior = scrollBehavior,
                    onSortClick = { showSortDialog = true },
                    onAddGroupClick = { showAddGroupDialog = true },
                    onSearchQueryChange = onSearchQueryChange,
                    onSearchFocusChange = onSearchFocusChange,
                    focusRequester = focusRequester,
                )
            },
            floatingActionButton = {
                ServicesFab(
                    isVisible = uiState.isLoading.not(),
                    isExtendedVisible = uiState.totalServices == 0,
                    isNormalVisible = reorderableState.listState.isScrollingUp(),
                    onClick = onFabClick,
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { padding ->

            LazyColumn(
                state = reorderableState.listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(TwTheme.color.background)
                    .padding(padding)
                    .reorderable(reorderableState),
                contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp),
                userScrollEnabled = uiState.services.isNotEmpty(),
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

                if (uiState.totalServices == 0 && uiState.totalGroups == 1) {
                    listItem(ServicesListItem.Empty) {
                        TwEmptyScreen(
                            body = TwLocale.strings.servicesEmptyBody,
                            image = painterResource(id = R.drawable.img_services_empty),
                            additionalContent = {
                                TwOutlinedButton(
                                    text = TwLocale.strings.servicesEmptyImportCta,
                                    onClick = onExternalImportClick,
                                )
                            },
                            modifier = Modifier
                                .fillParentMaxSize()
                                .animateItemPlacement()
                        )
                    }

                    return@LazyColumn
                }

                if (uiState.totalServices > 0 && uiState.totalGroups == 1 && uiState.services.isEmpty()) {
                    listItem(ServicesListItem.EmptySearch) {
                        TwEmptyScreen(
                            title = TwLocale.strings.servicesEmptySearch,
                            body = TwLocale.strings.servicesEmptySearchBody,
                            image = painterResource(id = R.drawable.img_services_empty_search),
                            modifier = Modifier
                                .fillParentMaxSize()
                                .animateItemPlacement(),
                        )
                    }

                    return@LazyColumn
                }

                reorderableData.value.forEach { item ->

                    when (item) {
                        ServicesListItem.SyncNoticeBar -> {
                            listItem(item) {
                                SyncNoticeBar(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    onOpenBackupClick = onOpenBackupClick,
                                )
                            }
                        }

                        ServicesListItem.SyncReminder -> {
                            listItem(item) {
                                SyncReminder(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    onOpenBackupClick = onOpenBackupClick,
                                    onDismissClick = onDismissSyncReminderClick,
                                )
                            }
                        }

                        is ServicesListItem.GroupItem -> {
                            val group = item.group

                            listItem(item) {
                                ServicesGroup(
                                    id = group.id,
                                    name = group.name ?: TwLocale.strings.servicesMyTokens,
                                    count = uiState.services.count { it.groupId == group.id },
                                    expanded = group.isExpanded,
                                    editMode = uiState.isInEditMode,
                                    modifier = Modifier
                                        .animateContentSize()
                                        .then(
                                            if (isDragging) {
                                                Modifier
                                            } else {
                                                Modifier.animateItemPlacement()
                                            }
                                        ),
                                    onClick = { onToggleGroupExpand(group.id) },
                                    onExpandClick = { onToggleGroupExpand(group.id) },
                                    onMoveUpClick = { onMoveUpGroup(group.id.orEmpty()) },
                                    onMoveDownClick = { onMoveDownGroup(group.id.orEmpty()) },
                                    onEditClick = {
                                        clickedGroup = group
                                        showEditGroupDialog = true
                                    },
                                    onDeleteClick = {
                                        clickedGroup = group
                                        showDeleteGroupDialog = true
                                    },
                                )
                            }
                        }

                        is ServicesListItem.ServiceItem -> {
                            val service = item.service

                            listItem(item) {
                                ReorderableItem(
                                    state = reorderableState,
                                    key = item.key,
                                    modifier = Modifier
                                        .animateContentSize()
                                        .then(
                                            if (isDragging) {
                                                Modifier
                                            } else {
                                                Modifier.animateItemPlacement()
                                            }
                                        ),
                                ) { isDragging ->
                                    val state = service.asState()

                                    DsService(
                                        state = state,
                                        style = when (uiState.appSettings.servicesStyle) {
                                            ServicesStyle.Default -> ServiceStyle.Default
                                            ServicesStyle.Compact -> ServiceStyle.Compact
                                        },
                                        editMode = uiState.isInEditMode,
                                        showNextCode = uiState.appSettings.showNextCode,
                                        containerColor = if (recentlyAddedService == service.id) {
                                            serviceContainerColorBlinking.value
                                        } else {
                                            serviceContainerColor
                                        },
                                        modifier = Modifier,
                                        dragHandleVisible = uiState.appSettings.servicesSort == ServicesSort.Manual,
                                        dragModifier = Modifier.detectReorder(state = reorderableState),
                                        onLongClick = {
                                            keyboardController?.hide()
                                            scope.launch {
                                                modalType = ModalType.FocusService(service.id, false)
                                                modalState.show()
                                            }
                                        },
                                        onClick = {
                                            state.copyToClipboard(activity, uiState.appSettings.showNextCode)
                                        },
                                        onIncrementCounterClick = {
                                            onIncrementHotpCounterClick(service)
                                        }
                                    )
                                }
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    if (showAddGroupDialog) {
        InputDialog(title = TwLocale.strings.groupsAdd,
            onDismissRequest = { showAddGroupDialog = false },
            positive = TwLocale.strings.commonAdd,
            negative = TwLocale.strings.commonCancel,
            hint = TwLocale.strings.groupsName,
            showCounter = true,
            minLength = 1,
            maxLength = 32,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
            ),
            onPositiveClick = { onAddGroup(it) })
    }

    if (showEditGroupDialog) {
        InputDialog(title = TwLocale.strings.groupsEdit,
            onDismissRequest = { showEditGroupDialog = false },
            positive = TwLocale.strings.commonSave,
            negative = TwLocale.strings.commonCancel,
            hint = TwLocale.strings.groupsName,
            prefill = clickedGroup?.name.orEmpty(),
            showCounter = true,
            minLength = 1,
            maxLength = 32,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
            ),
            onPositiveClick = { onEditGroup(clickedGroup?.id.orEmpty(), it) })
    }

    if (showDeleteGroupDialog) {
        ConfirmDialog(
            onDismissRequest = { showDeleteGroupDialog = false },
            title = TwLocale.strings.commonDelete,
            body = TwLocale.strings.groupsDelete,
            onConfirm = { onDeleteGroup(clickedGroup?.id.orEmpty()) },
        )
    }

    if (showSortDialog) {
        ListRadioDialog(
            onDismissRequest = { showSortDialog = false },
            title = TwLocale.strings.servicesSortBy,
            options = TwLocale.strings.servicesSortByOptions,
            selectedIndex = when (uiState.appSettings.servicesSort) {
                ServicesSort.Alphabetical -> 0
                ServicesSort.Manual -> 1
            },
            onOptionSelected = { index, _ -> onSortChange(index) }
        )
    }

    if (showQrFromGalleryDialog) {
        ConfirmDialog(
            onDismissRequest = { showQrFromGalleryDialog = false },
            title = TwLocale.strings.servicesQrFromGalleryTitle,
            positive = TwLocale.strings.servicesQrFromGalleryCta,
            negative = null,
            bodyAnnotated = buildAnnotatedString {
                append(TwLocale.strings.servicesQrFromGalleryBody1)

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(TwLocale.strings.servicesQrFromGalleryBody2)
                }

                append(TwLocale.strings.servicesQrFromGalleryBody3)
            }
        )
    }

    if (askForPermission) {
        RequestPermission(
            permission = Manifest.permission.CAMERA,
            onGranted = {
                askForPermission = false
                listener.openAddQrService(activity)
            },
            onDismissRequest = { askForPermission = false },
            rationaleTitle = TwLocale.strings.permissionCameraTitle,
            rationaleText = TwLocale.strings.permissionCameraBody,
        )
    }
}

