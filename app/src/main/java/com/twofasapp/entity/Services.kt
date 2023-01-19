package com.twofasapp.entity

data class Services(
    val groups: List<GroupModel>,
) {

    fun isEmpty() = groups.size == 1 && groups[0].services.isEmpty()

    fun hasGroups() = groups.size > 1
}