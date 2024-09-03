package com.twofasapp.feature.home.ui.services

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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.session.domain.ServicesSort
import com.twofasapp.data.session.domain.ServicesStyle
import com.twofasapp.designsystem.TwTheme
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
import com.twofasapp.designsystem.service.asState
import com.twofasapp.feature.home.R
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.ui.bottombar.BottomBar
import com.twofasapp.feature.home.ui.bottombar.BottomBarListener
import com.twofasapp.feature.home.ui.services.component.ServicesAppBar
import com.twofasapp.feature.home.ui.services.component.ServicesFab
import com.twofasapp.feature.home.ui.services.component.ServicesProgress
import com.twofasapp.feature.home.ui.services.component.SyncNoticeBar
import com.twofasapp.feature.home.ui.services.component.SyncReminder
import com.twofasapp.locale.TwLocale
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ServicesScreen(
        uiState = uiState,
        listener = listener,
        bottomBarListener = bottomBarListener,
        onEventConsumed = { viewModel.consumeEvent(it) },
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
        onOpenBackupClick = { listener.openBackup(it) },
        onDismissSyncReminderClick = { viewModel.dismissSyncReminder() },
        onIncrementHotpCounterClick = { viewModel.incrementHotpCounter(it) },
        onRevealClick = { viewModel.reveal(it) }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ServicesScreen(
    uiState: ServicesUiState,
    listener: HomeNavigationListener,
    bottomBarListener: BottomBarListener,
    onEventConsumed: (ServicesUiEvent) -> Unit,
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
    onOpenBackupClick: (Boolean) -> Unit = {},
    onDismissSyncReminderClick: () -> Unit = {},
    onIncrementHotpCounterClick: (Service) -> Unit = {},
    onRevealClick: (Service) -> Unit = {},
) {

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var showAddGroupDialog by remember { mutableStateOf(false) }
    var showEditGroupDialog by remember { mutableStateOf(false) }
    var showDeleteGroupDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showQrFromGalleryDialog by remember { mutableStateOf(false) }
    var clickedGroup by remember { mutableStateOf<Group?>(null) }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
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
            onDragEnd(reorderableData.value)
            scope.launch {
                delay(500)
                isDragging = false
            }
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
            ServicesUiEvent.ShowQrFromGalleryDialog -> {
                showQrFromGalleryDialog = true
            }

            is ServicesUiEvent.ServiceAdded -> {
                val serviceId = it.id
                val service = uiState.services.firstOrNull { it.id == serviceId }

                if (service != null) {
                    if (uiState.groups.firstOrNull { it.id == service.groupId }?.isExpanded == true) {
                        scope.launch {
                            val serviceIndex = uiState.items.indexOfFirst {
                                it is ServicesListItem.ServiceItem && it.service.id == serviceId
                            }

                            if (serviceIndex < 0) {
                                return@launch
                            }

                            listState.animateScrollToItem(serviceIndex)
                            recentlyAddedService = serviceId

                            serviceContainerColorBlinking.animateTo(serviceContainerColorBlink, tween(0))
                            serviceContainerColorBlinking.animateTo(serviceContainerColor, tween(2000, easing = EaseOut))

                            recentlyAddedService = null
                        }
                    }
                }
            }

            is ServicesUiEvent.OpenImport -> listener.openBackupImport(it.filePath)
        }

        onEventConsumed(it)
    }

    LaunchedEffect(Unit) {
        if (uiState.searchFocused) {
            awaitFrame()
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(uiState.searchFocused) {
        if (uiState.searchFocused.not()) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { it }
            .collect { isScrollInProgress ->
                if (isScrollInProgress) {
                    onSearchFocusChange(false)
                }
            }
    }

    BackHandler(
        enabled = uiState.isInEditMode || uiState.searchFocused
    ) {
        when {
            uiState.isInEditMode -> onEditModeChange()
            uiState.searchQuery.isNotEmpty() -> onSearchQueryChange("")
            uiState.searchFocused -> onSearchFocusChange(false)
        }
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                selectedIndex = 0,
                listener = bottomBarListener,
                onItemClick = {
                    if (uiState.searchFocused) {
                        onSearchFocusChange(false)
                    }
                }
            )
        },
        topBar = {
            ServicesAppBar(
                query = uiState.searchQuery,
                isInEditMode = uiState.isInEditMode,
                isSearchFocused = uiState.searchFocused,
                hasUnreadNotifications = uiState.hasUnreadNotifications,
                onEditModeChange = onEditModeChange,
                scrollBehavior = scrollBehavior,
                onSortClick = { showSortDialog = true },
                onAddGroupClick = { showAddGroupDialog = true },
                onNotificationsClick = {
                    onSearchFocusChange(false)
                    listener.openNotifications()
                },
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
                onClick = {
                    onSearchFocusChange(false)
                    listener.openAddServiceModal()
                },
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
            userScrollEnabled = uiState.services.isNotEmpty() || (uiState.searchQuery.isNotBlank() && uiState.groups.size > 1),
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
            if (uiState.services.isEmpty() && uiState.totalGroups == 1 && uiState.searchQuery.isNotEmpty()) {
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

            reorderableData.value.forEach { item ->

                when (item) {
                    ServicesListItem.SyncNoticeBar -> {
                        listItem(item) {
                            SyncNoticeBar(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                onOpenBackupClick = { onOpenBackupClick(false) },
                            )
                        }
                    }

                    ServicesListItem.SyncReminder -> {
                        listItem(item) {
                            SyncReminder(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                onOpenBackupClick = { onOpenBackupClick(true) },
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
                            ) { _ ->
                                val state = service.asState()

                                DsService(
                                    state = state,
                                    modifier = Modifier,
                                    style = when (uiState.appSettings.servicesStyle) {
                                        ServicesStyle.Default -> ServiceStyle.Default
                                        ServicesStyle.Compact -> ServiceStyle.Compact
                                    },
                                    editMode = uiState.isInEditMode,
                                    showNextCode = uiState.appSettings.showNextCode,
                                    hideCodes = uiState.appSettings.hideCodes,
                                    containerColor = if (recentlyAddedService == service.id) {
                                        serviceContainerColorBlinking.value
                                    } else {
                                        serviceContainerColor
                                    },
                                    dragHandleVisible = uiState.appSettings.servicesSort == ServicesSort.Manual,
                                    dragModifier = Modifier.detectReorder(state = reorderableState),
                                    onClick = { state.copyToClipboard(activity, uiState.appSettings.showNextCode) },
                                    onLongClick = {
                                        keyboardController?.hide()
                                        listener.openFocusServiceModal(service.id)
                                    },
                                    onIncrementCounterClick = { onIncrementHotpCounterClick(service) },
                                    onRevealClick = { onRevealClick(service) }
                                )
                            }
                        }
                    }

                    else -> Unit
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
            onPositive = { onDeleteGroup(clickedGroup?.id.orEmpty()) },
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

//    if (askForPermission) {
//        RequestPermission(
//            permission = Manifest.permission.CAMERA,
//            onGranted = {
//                askForPermission = false
//                listener.openAddServiceModal()
//            },
//            onDismissRequest = { askForPermission = false },
//            rationaleTitle = TwLocale.strings.permissionCameraTitle,
//            rationaleText = TwLocale.strings.permissionCameraBody,
//        )
//    }
}

