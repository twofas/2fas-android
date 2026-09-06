package com.twofasapp.feature.home.ui.services

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.ServiceCard
import com.twofasapp.core.design.feature.items.ServiceStyle
import com.twofasapp.core.design.feature.items.ServicesGroup
import com.twofasapp.core.design.feature.items.asState
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.dialog.InputDialog
import com.twofasapp.core.design.foundation.dialog.InputValidation
import com.twofasapp.core.design.foundation.dialog.ListRadioDialog
import com.twofasapp.core.design.foundation.lazy.isScrollingUp
import com.twofasapp.core.design.foundation.lazy.listItem
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.progress.CircularProgressIndicator
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.core.design.ktx.openSafely
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.services.domain.QueuedAddServiceModal
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.data.session.domain.ServicesSort
import com.twofasapp.data.session.domain.ServicesStyle
import com.twofasapp.feature.home.ui.services.add.manual.AddServiceManualModal
import com.twofasapp.feature.home.ui.services.add.scan.AddServiceScanModal
import com.twofasapp.feature.home.ui.services.component.AppReviewItem
import com.twofasapp.feature.home.ui.services.component.AppReviewViewModel
import com.twofasapp.feature.home.ui.services.component.CloudSyncItem
import com.twofasapp.feature.home.ui.services.component.HomeAppBar
import com.twofasapp.feature.home.ui.services.component.HomeEmpty
import com.twofasapp.feature.home.ui.services.component.HomeFab
import com.twofasapp.feature.home.ui.services.component.HomeSearchEmpty
import com.twofasapp.feature.home.ui.services.component.PassBannerItem
import com.twofasapp.feature.home.ui.services.focus.ServiceModal
import com.twofasapp.locale.MdtLocale
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    appReviewViewModel: AppReviewViewModel = koinViewModel(),
    navigator: Navigator = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddServiceScanModal by remember { mutableStateOf(false) }
    var showAddServiceManualModal by remember { mutableStateOf(false) }
    var addedServiceInModal by remember { mutableStateOf<RecentlyAddedService?>(null) }
    var focusServiceId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        when (viewModel.consumeQueuedAddServiceModal()) {
            QueuedAddServiceModal.Scan -> showAddServiceScanModal = true
            QueuedAddServiceModal.Manual -> showAddServiceManualModal = true
            null -> Unit
        }
    }

    Content(
        uiState = uiState,
        onEventConsumed = { viewModel.consumeEvent(it) },
        onExternalImportClick = { navigator.open(Screen.ExternalImportSelector) },
        onEditModeChange = { viewModel.toggleEditMode() },
        onToggleServiceSelection = { viewModel.toggleServiceSelection(it) },
        onDeleteSelectedServices = { viewModel.deleteSelectedServices() },
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
        onOpenBackupClick = { navigator.open(Screen.Backup) },
        onOpenBackupImport = { navigator.open(Screen.BackupImport(importFileUri = it)) },
        onOpenNotifications = { navigator.open(Screen.Notifications) },
        onOpenDeveloper = { navigator.open(Screen.Developer) },
        onOpenAddServiceModal = { showAddServiceScanModal = true },
        onOpenFocusService = { focusServiceId = it },
        onRateAppClick = { appReviewViewModel.rate(it) },
        onDismissAppReviewClick = { appReviewViewModel.dismiss() },
        onDismissPassBannerClick = { viewModel.dismissPassBanner() },
        onDisablePassBannerClick = { viewModel.disablePassBanner() },
        onIncrementHotpCounterClick = { viewModel.incrementHotpCounter(it) },
        onRevealClick = { viewModel.reveal(it) },
    )

    if (showAddServiceScanModal) {
        AddServiceScanModal(
            onDismissRequest = {
                showAddServiceScanModal = false
                addedServiceInModal?.let { viewModel.notifyServiceAdded(it) }
                addedServiceInModal = null
            },
            openManual = { showAddServiceManualModal = true },
            openGuides = { navigator.open(Screen.Guides) },
            onAddedSuccessfully = { addedServiceInModal = it },
        )
    }

    if (showAddServiceManualModal) {
        AddServiceManualModal(
            onDismissRequest = {
                showAddServiceManualModal = false
                addedServiceInModal?.let { viewModel.notifyServiceAdded(it) }
                addedServiceInModal = null
            },
            onAddedSuccessfully = { addedServiceInModal = it },
        )
    }

    focusServiceId?.let { serviceId ->
        ServiceModal(
            serviceId = serviceId,
            onDismissRequest = { focusServiceId = null },
            openService = { navigator.open(Screen.EditService(it)) },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    uiState: HomeUiState,
    onEventConsumed: (HomeUiEvent) -> Unit,
    onExternalImportClick: () -> Unit = {},
    onEditModeChange: () -> Unit = {},
    onToggleServiceSelection: (Long) -> Unit = {},
    onDeleteSelectedServices: () -> Unit = {},
    onToggleGroupExpand: (String?) -> Unit = {},
    onAddGroup: (String) -> Unit = {},
    onMoveUpGroup: (String) -> Unit = {},
    onMoveDownGroup: (String) -> Unit = {},
    onEditGroup: (String, String) -> Unit = { _, _ -> },
    onDeleteGroup: (String) -> Unit = {},
    onDragStart: () -> Unit = { },
    onDragEnd: (List<HomeListItem>) -> Unit = { },
    onSortChange: (Int) -> Unit = {},
    onSearchQueryChange: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    onOpenBackupClick: (Boolean) -> Unit = {},
    onOpenBackupImport: (String?) -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenDeveloper: () -> Unit = {},
    onOpenAddServiceModal: () -> Unit = {},
    onOpenFocusService: (Long) -> Unit = {},
    onRateAppClick: (Activity) -> Unit = {},
    onDismissAppReviewClick: () -> Unit = {},
    onDismissPassBannerClick: () -> Unit = {},
    onDisablePassBannerClick: () -> Unit = {},
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
    val activity = LocalContext.currentActivity
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    var isDragging by remember { mutableStateOf(false) }
    val reorderableData = remember { mutableStateOf(uiState.items) }
    val listState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(
        listState = listState,
        onMove = { from, to ->
            onDragStart()
            isDragging = true
            val fromItem = reorderableData.value[from.index]
            val toItem = reorderableData.value[to.index]
            if (fromItem is HomeListItem.ServiceItem) {
                if (toItem is HomeListItem.ServiceItem || (toItem is HomeListItem.GroupItem && toItem.group.id != null)) {
                    reorderableData.value = reorderableData.value.toMutableList().apply {
                        add(to.index, removeAt(from.index))
                    }
                }
            }
        },
        onDragEnd = { _, _ ->
            onDragEnd(reorderableData.value)
            scope.launch {
                delay(500.milliseconds)
                isDragging = false
            }
        },
    )

    val serviceContainerColor = MdtTheme.color.surfaceContainer
    val serviceContainerColorBlink = MdtTheme.color.surfaceContainerHighest
    val serviceContainerColorBlinking = remember { Animatable(serviceContainerColor) }

    var recentlyAddedService by remember { mutableStateOf<Long?>(null) }
    reorderableData.value = uiState.items

    uiState.events.firstOrNull()?.let {
        when (it) {
            HomeUiEvent.ShowQrFromGalleryDialog -> {
                showQrFromGalleryDialog = true
            }

            is HomeUiEvent.ServiceAdded -> {
                val serviceId = it.id
                val service = uiState.services.firstOrNull { it.id == serviceId }

                if (service != null) {
                    if (uiState.groups.firstOrNull { it.id == service.groupId }?.isExpanded == true) {
                        scope.launch {
                            val serviceIndex = uiState.items.indexOfFirst {
                                it is HomeListItem.ServiceItem && it.service.id == serviceId
                            }

                            if (serviceIndex < 0) {
                                return@launch
                            }

                            listState.animateScrollToItem(serviceIndex)
                            recentlyAddedService = serviceId

                            serviceContainerColorBlinking.animateTo(serviceContainerColorBlink, tween(1000))
                            serviceContainerColorBlinking.animateTo(serviceContainerColor, tween(2000, easing = EaseOut))

                            recentlyAddedService = null
                        }
                    }
                }
            }

            is HomeUiEvent.OpenImport -> onOpenBackupImport(it.filePath)
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
        enabled = uiState.isInEditMode || uiState.searchFocused,
    ) {
        when {
            uiState.isInEditMode -> onEditModeChange()
            uiState.searchQuery.isNotEmpty() -> onSearchQueryChange("")
            uiState.searchFocused -> onSearchFocusChange(false)
        }
    }

    Scaffold(
        topBar = {
            HomeAppBar(
                query = uiState.searchQuery,
                isInEditMode = uiState.isInEditMode,
                isSearchFocused = uiState.searchFocused,
                isListEmpty = uiState.totalServices == 0 && uiState.totalGroups <= 1 && uiState.isLoading.not(),
                hasUnreadNotifications = uiState.hasUnreadNotifications,
                developerModeEnabled = uiState.developerModeEnabled,
                selectedServices = uiState.services.filter { uiState.selectedServiceIds.contains(it.id) },
                onEditModeChange = onEditModeChange,
                onSortClick = { showSortDialog = true },
                onAddGroupClick = { showAddGroupDialog = true },
                onDeleteSelectedConfirmed = onDeleteSelectedServices,
                onNotificationsClick = {
                    onSearchFocusChange(false)
                    onOpenNotifications()
                },
                onDeveloperClick = {
                    onSearchFocusChange(false)
                    onOpenDeveloper()
                },
                onSearchQueryChange = onSearchQueryChange,
                onSearchFocusChange = onSearchFocusChange,
                focusRequester = focusRequester,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MdtTheme.color.background)
                .padding(top = padding.calculateTopPadding()),
        ) {
            LazyColumn(
                state = reorderableState.listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MdtTheme.color.background)
                    .reorderable(reorderableState),
                contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp),
                userScrollEnabled = uiState.services.isNotEmpty() || (uiState.searchQuery.isNotBlank() && uiState.groups.size > 1),
            ) {
                if (uiState.isLoading) {
                    listItem(HomeListItem.Loader) {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .animateItem(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    return@LazyColumn
                }
                if (uiState.services.isEmpty() && uiState.totalGroups == 1 && uiState.searchQuery.isNotEmpty()) {
                    listItem(HomeListItem.EmptySearch) {
                        HomeSearchEmpty(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .animateItem(),
                        )
                    }

                    return@LazyColumn
                }

                if (uiState.totalServices == 0 && uiState.totalGroups == 1) {
                    listItem(HomeListItem.Empty) {
                        HomeEmpty(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .animateItem()
                                .padding(horizontal = 16.dp),
                            onExternalImportClick = onExternalImportClick,
                        )
                    }

                    return@LazyColumn
                }

                reorderableData.value.forEach { item ->

                    when (item) {
                        HomeListItem.CloudSyncItem -> {
                            listItem(item) {
                                CloudSyncItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    onOpenBackupClick = { onOpenBackupClick(false) },
                                )
                            }
                        }

                        HomeListItem.AppReview -> {
                            listItem(item) {
                                AppReviewItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    onRateClick = { onRateAppClick(activity) },
                                    onDismissClick = onDismissAppReviewClick,
                                )
                            }
                        }

                        HomeListItem.PassBanner -> {
                            listItem(item) {
                                PassBannerItem(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .padding(bottom = 12.dp, top = 4.dp),
                                    onGoToStoreClick = {
                                        uriHandler.openSafely(MdtLocale.links.passPlayStore, activity)
                                        onDismissPassBannerClick()
                                    },
                                    onDismissClick = onDisablePassBannerClick,
                                )
                            }
                        }

                        is HomeListItem.GroupItem -> {
                            val group = item.group

                            listItem(item) {
                                ServicesGroup(
                                    id = group.id,
                                    name = group.name ?: MdtLocale.strings.servicesMyTokens,
                                    count = uiState.services.count { it.groupId == group.id },
                                    expanded = group.isExpanded,
                                    editMode = uiState.isInEditMode,
                                    modifier = Modifier
                                        .animateContentSize()
                                        .then(
                                            if (isDragging) {
                                                Modifier
                                            } else {
                                                Modifier.animateItem()
                                            },
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

                        is HomeListItem.ServiceItem -> {
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
                                                Modifier.animateItem()
                                            },
                                        ),
                                ) { _ ->
                                    val state = service.asState()

                                    ServiceCard(
                                        state = state,
                                        modifier = Modifier,
                                        style = when (uiState.servicesStyle) {
                                            ServicesStyle.Default -> ServiceStyle.Default
                                            ServicesStyle.Compact -> ServiceStyle.Compact
                                        },
                                        editMode = uiState.isInEditMode,
                                        selected = uiState.selectedServiceIds.contains(service.id),
                                        showNextCode = uiState.showNextCode,
                                        hideCodes = uiState.hideCodes,
                                        containerColor = if (recentlyAddedService == service.id) {
                                            serviceContainerColorBlinking.value
                                        } else {
                                            serviceContainerColor
                                        },
                                        dragHandleVisible = uiState.servicesSort == ServicesSort.Manual,
                                        dragModifier = Modifier.detectReorder(state = reorderableState),
                                        onClick = {
                                            if (uiState.isInEditMode) {
                                                onToggleServiceSelection(service.id)
                                            } else {
                                                state.copyToClipboard(activity, uiState.showNextCode)
                                            }
                                        },
                                        onLongClick = {
                                            keyboardController?.hide()
                                            onOpenFocusService(service.id)
                                        },
                                        onIncrementCounterClick = { onIncrementHotpCounterClick(service) },
                                        onRevealClick = { onRevealClick(service) },
                                    )
                                }
                            }
                        }

                        else -> Unit
                    }
                }
            }

            HomeFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                isVisible = uiState.isLoading.not() && uiState.isInEditMode.not(),
                isExtendedVisible = uiState.totalServices == 0,
                isNormalVisible = reorderableState.listState.isScrollingUp(),
                onClick = {
                    onSearchFocusChange(false)
                    onOpenAddServiceModal()
                },
            )
        }
    }

    if (showAddGroupDialog) {
        InputDialog(
            onDismissRequest = { showAddGroupDialog = false },
            title = MdtLocale.strings.groupsAdd,
            label = MdtLocale.strings.groupsName,
            positive = MdtLocale.strings.commonAdd,
            negative = MdtLocale.strings.commonCancel,
            validate = { if (it.trim().length in 1..32) InputValidation.Valid else InputValidation.Invalid(null) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
            ),
            onPositive = { onAddGroup(it.trim()) },
        )
    }

    if (showEditGroupDialog) {
        InputDialog(
            onDismissRequest = { showEditGroupDialog = false },
            title = MdtLocale.strings.groupsEdit,
            label = MdtLocale.strings.groupsName,
            prefill = clickedGroup?.name.orEmpty(),
            positive = MdtLocale.strings.commonSave,
            negative = MdtLocale.strings.commonCancel,
            validate = { if (it.trim().length in 1..32) InputValidation.Valid else InputValidation.Invalid(null) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
            ),
            onPositive = { onEditGroup(clickedGroup?.id.orEmpty(), it.trim()) },
        )
    }

    if (showDeleteGroupDialog) {
        ConfirmDialog(
            onDismissRequest = { showDeleteGroupDialog = false },
            title = MdtLocale.strings.commonDelete,
            body = MdtLocale.strings.groupsDelete,
            onPositive = { onDeleteGroup(clickedGroup?.id.orEmpty()) },
        )
    }

    if (showSortDialog) {
        ListRadioDialog(
            onDismissRequest = { showSortDialog = false },
            title = MdtLocale.strings.servicesSortBy,
            options = MdtLocale.strings.servicesSortByOptions,
            selectedIndex = when (uiState.servicesSort) {
                ServicesSort.Alphabetical -> 0
                ServicesSort.Manual -> 1
            },
            onOptionSelected = { index, _ -> onSortChange(index) },
        )
    }

    if (showQrFromGalleryDialog) {
        ConfirmDialog(
            onDismissRequest = { showQrFromGalleryDialog = false },
            title = MdtLocale.strings.servicesQrFromGalleryTitle,
            positive = MdtLocale.strings.servicesQrFromGalleryCta,
            negative = null,
            bodyAnnotated = buildAnnotatedString {
                append(MdtLocale.strings.servicesQrFromGalleryBody1)

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(MdtLocale.strings.servicesQrFromGalleryBody2)
                }

                append(MdtLocale.strings.servicesQrFromGalleryBody3)
            },
        )
    }
}

@Preview
@Composable
private fun Preview() {
    val services = listOf(
        Service.Preview.copy(id = 1L, name = "Google", info = "john.doe@gmail.com", labelText = "GO", labelColor = Service.Tint.Red),
        Service.Preview.copy(id = 2L, name = "GitHub", info = "johndoe", labelText = "GH", labelColor = Service.Tint.Purple),
        Service.Preview.copy(id = 3L, name = "Amazon", info = "john.doe@gmail.com", labelText = "AM", labelColor = Service.Tint.Orange),
    )

    PreviewTheme {
        Content(
            uiState = HomeUiState(
                services = services,
                isLoading = false,
                totalServices = services.size,
                totalGroups = 1,
                items = services.map { HomeListItem.ServiceItem(it) },
            ),
            onEventConsumed = {},
            onSearchQueryChange = {},
            onSearchFocusChange = {},
        )
    }
}