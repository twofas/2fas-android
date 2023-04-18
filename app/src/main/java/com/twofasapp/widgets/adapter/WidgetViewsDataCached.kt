package com.twofasapp.widgets.adapter

import com.twofasapp.entity.ServiceModel
import com.twofasapp.usecases.services.GetServices
import com.twofasapp.usecases.services.ServicesModelMapper
import com.twofasapp.usecases.widgets.GetWidgetSettings

class WidgetViewsDataCached(
    private val getWidgetSettings: GetWidgetSettings,
    private val getServices: GetServices,
    private val servicesModelMapper: ServicesModelMapper,
) : WidgetViewsData {

    companion object {
        private const val CACHE_TIME = 2_000
    }

    private val cachedServices: MutableList<ServiceModel> = mutableListOf()
    private var cachedTime: Long = 0

    override fun getWidgetServices(appWidgetId: Int): List<com.twofasapp.prefs.model.Widget.Service> {
        return getWidgetSettings.execute().getWidgetForId(appWidgetId)?.services
            ?.filter { getServices.execute().blockingGet().map { dto -> dto.id }.contains(it.id) }
            ?: emptyList()
    }

    override fun getServiceModel(appWidgetId: Int, position: Int): ServiceModel? {
        try {
            val widgetService = getWidgetServices(appWidgetId)[position]
            return getCachedServices().firstOrNull { it.service.id == widgetService.id }
        } catch (e: Exception) {
            invalidateCache()
            val widgetService = getWidgetServices(appWidgetId)[position]
            return getCachedServices().firstOrNull { it.service.id == widgetService.id }
        }
    }

    override fun invalidateCache() {
        cachedTime = 0
    }

    private fun getCachedServices(): List<ServiceModel> {
        if (System.currentTimeMillis() - cachedTime > CACHE_TIME) {
            cachedServices.clear()
            cachedServices.addAll(
                servicesModelMapper.execute(getServices.execute().blockingGet()).blockingGet()
                    .groups.flatMap { it.services }
            )
            cachedTime = System.currentTimeMillis()
        }

        return cachedServices
    }
}