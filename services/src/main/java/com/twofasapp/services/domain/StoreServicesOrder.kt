package com.twofasapp.services.domain

import com.twofasapp.prefs.model.ServicesOrder
import com.twofasapp.prefs.usecase.ServicesOrderPreference

class StoreServicesOrder(private val servicesOrderPreference: ServicesOrderPreference) {

    fun getOrder(): ServicesOrder {
        return servicesOrderPreference.get()
    }

    fun saveOrder(order: ServicesOrder) {
        servicesOrderPreference.put(order)
    }

    fun addToOrder(id: Long) {
        val servicesOrder = getOrder()
        saveOrder(servicesOrder.copy(ids = servicesOrder.ids.plus(id)))
    }

    fun deleteFromOrder(id: Long) {
        val servicesOrder = getOrder()
        saveOrder(servicesOrder.copy(ids = servicesOrder.ids.minus(id)))
    }
}
