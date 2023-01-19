package com.twofasapp.features.services

import com.mikepenz.fastadapter.IItem
import com.twofasapp.resources.R
import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.core.analytics.AnalyticsService
import com.twofasapp.entity.GroupModel
import com.twofasapp.entity.ServiceModel
import com.twofasapp.entity.Services
import com.twofasapp.prefs.ScopedNavigator
import com.twofasapp.prefs.usecase.ServicesOrderPreference
import com.twofasapp.prefs.usecase.StoreGroups
import com.twofasapp.services.domain.EditServiceUseCase
import com.twofasapp.services.domain.ShowBackupNotice
import com.twofasapp.services.domain.StoreHotpServices
import com.twofasapp.services.domain.StoreServicesOrder
import com.twofasapp.services.domain.model.Service
import com.twofasapp.time.domain.RecalculateTimeDeltaCase
import com.twofasapp.usecases.services.EditStateObserver
import com.twofasapp.usecases.services.SearchStateObserver
import com.twofasapp.usecases.services.ServicesModelMapper
import com.twofasapp.usecases.services.ServicesObserver
import com.twofasapp.usecases.services.ServicesRefreshTrigger
import com.twofasapp.usecases.services.UpdateServiceGroup
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.combineLatest
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

class ServicesPresenter(
    private val view: ServicesContract.View,
    private val navigator: ScopedNavigator,
    private val servicesObserver: ServicesObserver,
    private val servicesModelMapper: ServicesModelMapper,
    private val analyticsService: AnalyticsService,
    private val storeServicesOrder: StoreServicesOrder,
    private val editStateObserver: EditStateObserver,
    private val searchStateObserver: SearchStateObserver,
    private val storeGroups: StoreGroups,
    private val storeHotpServices: StoreHotpServices,
    private val editService: EditServiceUseCase,
    private val updateServiceGroup: UpdateServiceGroup,
    private val syncSyncBackupDispatcher: SyncBackupWorkDispatcher,
    private val servicesProxy: ServicesProxy,
    private val showBackupNotice: ShowBackupNotice,
    private val servicesRefreshTrigger: ServicesRefreshTrigger,
    private val servicesOrderPreference: ServicesOrderPreference,
    private val recalculateTimeDeltaCase: RecalculateTimeDeltaCase,
) : ServicesContract.Presenter() {

    companion object {
        private const val TAG_COUNTER = "counter"
    }

    private data class Result(
        val services: Services,
        val query: String,
        val isSearchEnabled: Boolean,
        val isEditEnabled: Boolean,
    )

    override fun onViewAttached() {
        Flowables.combineLatest(
            servicesProxy.observe(),
            searchStateObserver.observeQuery().startWith(""),
            searchStateObserver.observeEnabled().startWith(false),
            editStateObserver.observe().startWith(false)
        ) { services, query, isSearchEnabled, isEditEnabled ->
            Result(services, query, isSearchEnabled, isEditEnabled)
        }
            .onBackpressureLatest()
            .subscribeBy { (services, query, isSearchEnabled, isEditEnabled) ->
                onRefresh(
                    services,
                    query,
                    isSearchEnabled,
                    isEditEnabled
                )
            }
            .addToDisposables()

        servicesObserver.execute()
            .combineLatest(servicesRefreshTrigger.observe().startWith(Unit))
            .doOnNext { recalculateTimeDeltaCase.invoke() }
            .flatMapSingle { servicesModelMapper.execute(it.first) }
            .doOnNext { servicesProxy.publish(it) }
            .doOnNext {
                if (it.isEmpty() && editStateObserver.isEditEnabled) {
                    editStateObserver.offer(false)
                }
            }
            .safelySubscribe()
    }

    override fun onResume() {
        refreshServices()
        startCounter()
    }

    override fun onPause() {
        stopCounter()
    }

    private fun refreshServices() {
        servicesRefreshTrigger.trigger()
    }

    private fun onRefresh(
        services: Services,
        query: String,
        isSearchEnabled: Boolean,
        isEditEnabled: Boolean
    ) {
        val items: MutableList<IItem<*>> = mutableListOf()
        val shouldShowBackupNotice = showBackupNotice.currentValue()

        if (services.isEmpty()) {
            items.add(NoServicesItem())
            view.setItems(items)
            return
        }

        val filteredServices = services.copy(
            groups = services.groups.map { group ->
                group.copy(
                    services = group.services.filterMatchingQuery(
                        query
                    )
                )
            }
        )

        if (filteredServices.hasGroups().not() && filteredServices.isEmpty()) {
            items.add(NoServicesMatchingQueryItem())
            view.setItems(items)
            return
        }

        filteredServices.groups.forEachIndexed { index, groupModel ->

            if (filteredServices.hasGroups()) {
                items.add(
                    groupModel.toGroupItem(
                        isEditEnabled = isEditEnabled,
                        isSearchEnabled = isSearchEnabled,
                        isFirst = index == 1,
                        isLast = index == filteredServices.groups.size - 1
                    )
                )
            }

            if (groupModel.group.isExpanded || isEditEnabled || isSearchEnabled) {
                items.addAll(groupModel.services.map { serviceModel ->
                    serviceModel.toServiceItem(
                        isEditEnabled = isEditEnabled,
                        isSearchEnabled = isSearchEnabled,
                        isAnyNoticeVisible = shouldShowBackupNotice
                    )
                })
            }
        }

        when {
            shouldShowBackupNotice -> items.add(0, createBackupNoticeItem())
        }

        view.setItems(items)
    }

    private fun createBackupNoticeItem() =
        BackupNoticeItem(
            onTurnOnClick = { navigator.openBackup(true) },
            onDismissClick = {
                showBackupNotice.save(false)
                refreshServices()
            }
        )

    private fun GroupModel.toGroupItem(
        isEditEnabled: Boolean,
        isSearchEnabled: Boolean,
        isFirst: Boolean,
        isLast: Boolean,
    ): GroupItem {
        return GroupItem(
            model = this,
            isInEditMode = isEditEnabled,
            isInSearchMode = isSearchEnabled,
            isFirst = isFirst,
            isLast = isLast,
            onToggleClick = {
                if (it.group.id == null) {
                    storeGroups.toggleDefaultGroupExpanded()
                    refreshServices()
                } else {
                    storeGroups.edit(it.group.copy(isExpanded = it.group.isExpanded.not()))
                    refreshServices()
                }
            },
            onEditClick = {
                view.showEditGroupDialog(group) { editedGroup, newName ->
                    if (newName != editedGroup.name) {
                        storeGroups.edit(editedGroup.copy(name = newName))
                        refreshServices()
                    }
                }
            },
            onDeleteClick = {
                view.showConfirmGroupDelete(it) {
                    updateServiceGroup.execute(
                        it.services.map { serviceModel -> serviceModel.service },
                        null
                    )
                        .subscribeBy {
                            storeGroups.delete(it.group)
                            refreshServices()
                            syncSyncBackupDispatcher.dispatch(SyncBackupTrigger.GROUPS_CHANGED)
                            analyticsService.captureEvent(com.twofasapp.core.analytics.AnalyticsEvent.GROUP_REMOVE)
                        }
                        .addToDisposables()
                }
            },
            onMoveUp = {
                storeGroups.moveUp(it.group)
                refreshServices()
            },
            onMoveDown = {
                storeGroups.moveDown(it.group)
                refreshServices()
            }
        )
    }

    private fun ServiceModel.toServiceItem(
        isEditEnabled: Boolean,
        isSearchEnabled: Boolean,
        isAnyNoticeVisible: Boolean
    ): ServiceItem {
        return ServiceItem(
            model = this,
            isInEditMode = isEditEnabled,
            isInSearchMode = isSearchEnabled,
            isAnyNoticeVisible = isAnyNoticeVisible,
            onClick = {
                if (isEditEnabled) {
                    navigator.openShowService(it.service)
                } else {
                    onCopyToClipboard(this)
                }
            },
            onLongClick = {
                if (isEditEnabled.not()) {
                    view.showServiceBottomSheet(it)
                }
            },
            onDragTouch = { view.onDragTouch(this, it) },
            onRefreshCounterClick = {
                storeHotpServices.onRefreshCounter(it.service)
                editService.execute(
                    it.service.copy(
                        hotpCounter = (it.service.hotpCounter ?: Service.DefaultHotpCounter) + 1
                    )
                )
                    .safelySubscribe { refreshServices() }
            }
        )
    }

    private fun List<ServiceModel>.filterMatchingQuery(query: String): List<ServiceModel> {
        return filter {
            it.service.name.contains(query, true) ||
                    it.service.otpIssuer?.contains(query, true) ?: false ||
                    it.service.otpAccount?.contains(query, true) ?: false ||
                    it.tags.map { tag -> tag.lowercase() }.contains(query.lowercase())
        }
    }

    private fun onCopyToClipboard(model: ServiceModel) {
        if (isNextTokenDisplayed(model)) {
            view.copyToClipboard(model.nextCode)
            view.showSnackbarShort(R.string.tokens__next_copied_clipboard)
        } else {
            view.copyToClipboard(model.code)
            view.showSnackbarShort(R.string.tokens__copied_clipboard)
        }
    }

    override fun changeServiceOrder(
        serviceItem: ServiceItem,
        newPosition: Int,
        items: List<IItem<*>>
    ) {
        val order = servicesOrderPreference.get()

        if (order.type == com.twofasapp.prefs.model.ServicesOrder.Type.Manual) {
            storeServicesOrder.saveOrder(
                order.copy(
                    ids = items.filterIsInstance<ServiceItem>().map { it.model.service.id })
            )
        }

        if (items.filterIsInstance<GroupItem>().isEmpty()) {
            refreshServices()
            return
        }

        val item = items[newPosition] as ServiceItem
        val group: GroupItem? =
            items.subList(0, newPosition).findLast { it is GroupItem } as? GroupItem

        if (group != null) {
            editService.execute(
                item.model.service.copy(groupId = group.model.group.id)
            )
                .safelySubscribe()
        } else {
            refreshServices()
        }
    }

    override fun changeGroupOrder(groupItem: GroupItem, newPosition: Int, items: List<IItem<*>>) {
        storeGroups.updateOrder(
            items.filterIsInstance<GroupItem>()
                .map { it.model.group }
                .filter { it.id != null }
        )

        refreshServices()
    }

    private fun startCounter() {
        stopCounter()
        Flowable.interval(1000, TimeUnit.MILLISECONDS)
            .combineLatest(editStateObserver.observe().startWith(false))
            .filter { it.second.not() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ refreshServices() }, {})
            .addToDisposables(tag = TAG_COUNTER)
    }

    private fun stopCounter() {
        clearDisposables(tag = TAG_COUNTER)
    }

    private fun isNextTokenDisplayed(serviceModel: ServiceModel) =
        serviceModel.shouldShowNextToken && serviceModel.counter <= 5

}
