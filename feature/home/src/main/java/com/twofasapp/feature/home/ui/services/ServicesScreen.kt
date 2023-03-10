package com.twofasapp.feature.home.ui.services

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.data.services.domain.Group
import com.twofasapp.data.session.domain.ServicesSort
import com.twofasapp.data.session.domain.ServicesStyle
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.ModalBottomSheet
import com.twofasapp.designsystem.common.isScrollingUp
import com.twofasapp.designsystem.dialog.InputDialog
import com.twofasapp.designsystem.dialog.ListRadioDialog
import com.twofasapp.designsystem.group.ServicesGroup
import com.twofasapp.designsystem.ktx.copyToClipboard
import com.twofasapp.designsystem.ktx.currentActivity
import com.twofasapp.designsystem.lazy.listItem
import com.twofasapp.designsystem.service.DsService
import com.twofasapp.designsystem.service.ServiceStyle
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.ui.bottombar.BottomBar
import com.twofasapp.feature.home.ui.bottombar.BottomBarListener
import com.twofasapp.feature.home.ui.services.component.ServicesAppBar
import com.twofasapp.feature.home.ui.services.component.ServicesEmpty
import com.twofasapp.feature.home.ui.services.component.ServicesFab
import com.twofasapp.feature.home.ui.services.component.ServicesProgress
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
        onToggleGroupExpand = { viewModel.toggleGroup(it) },
        onAddGroup = { viewModel.addGroup(it) },
        onMoveUpGroup = { viewModel.moveUpGroup(it) },
        onMoveDownGroup = { viewModel.moveDownGroup(it) },
        onEditGroup = { id, name -> viewModel.editGroup(id, name) },
        onDeleteGroup = { viewModel.deleteGroup(it) },
        onSwapServices = { from, to -> viewModel.swapServices(from, to) },
        onSortChange = { viewModel.updateSort(it) },
        onSearchQueryChange = { viewModel.search(it) },
        onSearchFocusChange = { viewModel.searchFocused(it) }
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
    onToggleGroupExpand: (String?) -> Unit = {},
    onAddGroup: (String) -> Unit = {},
    onMoveUpGroup: (String) -> Unit = {},
    onMoveDownGroup: (String) -> Unit = {},
    onEditGroup: (String, String) -> Unit = { _, _ -> },
    onDeleteGroup: (String) -> Unit = {},
    onSwapServices: (Long, Long) -> Unit = { _, _ -> },
    onSortChange: (Int) -> Unit = {},
    onSearchQueryChange: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
) {

    val focusRequester = remember { FocusRequester() }
    var showAddGroupDialog by remember { mutableStateOf(false) }
    var showEditGroupDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var clickedGroup by remember { mutableStateOf<Group?>(null) }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    var modalType by remember { mutableStateOf<ModalType>(ModalType.AddService) }
    val activity = LocalContext.currentActivity
    val scope = rememberCoroutineScope()

    val data = remember { mutableStateOf(List(100) { "Item $it" }) }
    val listState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(listState = listState, onMove = { from, to ->
        println("order: $from -> $to")
        onSwapServices((from.key as String).split(":")[1].toLong(), (to.key as String).split(":")[1].toLong())
//        println("order: ${listState.layoutInfo.visibleItemsInfo.map { it.key }}")
//        data.value = data.value.toMutableList().apply {
//            add(to.index, removeAt(from.index))
//        }
    })

    uiState.events.firstOrNull()?.let {
        when (it) {
            ServicesStateEvent.ShowAddServiceModal -> {
                modalType = ModalType.AddService
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
                            showNextCode = uiState.appSettings.showNextCode,
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
                    isExtendedVisible = uiState.services.isEmpty(),
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

                if (uiState.services.isEmpty() && uiState.groups.isEmpty() && uiState.searchQuery.isEmpty()) {
                    listItem(ServicesListItem.Empty) {
                        ServicesEmpty(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .animateItemPlacement(), onExternalImportClick = onExternalImportClick
                        )
                    }
                }

                uiState.groups.forEach { group ->

                    if (uiState.groups.size > 1) {
                        listItem(ServicesListItem.Group(group.id)) {
                            ServicesGroup(
                                id = group.id,
                                name = group.name ?: TwLocale.strings.servicesMyTokens,
                                count = uiState.services.count { it.groupId == group.id },
                                expanded = group.isExpanded,
                                editMode = uiState.isInEditMode,
                                modifier = Modifier
                                    .animateContentSize()
                                    .animateItemPlacement(),
                                onClick = { onToggleGroupExpand(group.id) },
                                onExpandClick = { onToggleGroupExpand(group.id) },
                                onMoveUpClick = { onMoveUpGroup(group.id.orEmpty()) },
                                onMoveDownClick = { onMoveDownGroup(group.id.orEmpty()) },
                                onEditClick = {
                                    clickedGroup = group
                                    showEditGroupDialog = true
                                },
                                onDeleteClick = { onDeleteGroup(group.id.orEmpty()) },
                            )
                        }
                    }

                    if (group.isExpanded) {
                        uiState.services.filter { it.groupId == group.id }.forEach { service ->
                            listItem(ServicesListItem.Service(service.id)) {

                                ReorderableItem(
                                    state = reorderableState,
                                    key = ServicesListItem.Service(service.id).key,
                                    modifier = Modifier
                                        .animateItemPlacement()
                                        .animateContentSize(),
                                ) { isDragging ->
                                    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)

                                    DsService(
                                        state = service.asState(),
                                        style = when (uiState.appSettings.servicesStyle) {
                                            ServicesStyle.Default -> ServiceStyle.Default
                                            ServicesStyle.Compact -> ServiceStyle.Compact
                                        },
                                        editMode = uiState.isInEditMode,
                                        showNextCode = uiState.appSettings.showNextCode,
                                        containerColor = if (uiState.groups.size == 1) {
                                            TwTheme.color.background
                                        } else {
                                            TwTheme.color.serviceBackgroundWithGroups
                                        },
                                        modifier = Modifier.shadow(elevation.value),
                                        dragHandleVisible = uiState.appSettings.servicesSort == ServicesSort.Manual,
                                        dragModifier = Modifier.detectReorder(state = reorderableState),
                                        onClick = {
                                            modalType = ModalType.FocusService(service.id)
                                            scope.launch { modalState.show() }
                                        },
                                        onLongClick = {
                                            activity.copyToClipboard(service.code?.current.toString())
                                        },
                                    )
                                }
                            }
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
    }
}

