package com.twofasapp.features.services

import com.mikepenz.fastadapter.IItem
import com.twofasapp.backup.domain.SyncBackupTrigger
import com.twofasapp.backup.domain.SyncBackupWorkDispatcher
import com.twofasapp.core.analytics.AnalyticsService
import com.twofasapp.prefs.usecase.ServicesOrderPreference
import com.twofasapp.prefs.usecase.StoreGroups
import com.twofasapp.services.domain.EditServiceUseCase
import com.twofasapp.services.domain.StoreServicesOrder

class ServicesPresenter(
    private val analyticsService: AnalyticsService,
    private val storeServicesOrder: StoreServicesOrder,
    private val storeGroups: StoreGroups,
    private val editService: EditServiceUseCase,
    private val syncSyncBackupDispatcher: SyncBackupWorkDispatcher,
    private val servicesOrderPreference: ServicesOrderPreference,
) : ServicesContract.Presenter() {


    fun onGroupDelete() {
        syncSyncBackupDispatcher.dispatch(SyncBackupTrigger.GROUPS_CHANGED)
        analyticsService.captureEvent(com.twofasapp.core.analytics.AnalyticsEvent.GROUP_REMOVE)
    }

    fun onHotpRefreshCounterClick() {
//        onRefreshCounterClick = {
//            storeHotpServices.onRefreshCounter(it.service)
//            editService.execute(
//                it.service.copy(
//                    hotpCounter = (it.service.hotpCounter ?: Service.DefaultHotpCounter) + 1
//                )
//            )/**/
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
//            refreshServices()
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
//            refreshServices()
        }
    }

    override fun changeGroupOrder(groupItem: GroupItem, newPosition: Int, items: List<IItem<*>>) {
        storeGroups.updateOrder(
            items.filterIsInstance<GroupItem>()
                .map { it.model.group }
                .filter { it.id != null }
        )

//        refreshServices()
    }

}
