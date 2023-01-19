package com.twofasapp.entity

data class GroupModel(
    val group: com.twofasapp.prefs.model.Group,
    val services: List<ServiceModel>,
) {
    fun isDefault() = group.id == null
}