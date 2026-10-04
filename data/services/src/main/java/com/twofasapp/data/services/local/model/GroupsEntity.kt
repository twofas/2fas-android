package com.twofasapp.data.services.local.model

import kotlinx.serialization.Serializable

@Serializable
internal data class GroupsEntity(
    val list: List<GroupEntity> = emptyList(),
    val isDefaultGroupExpanded: Boolean = true,
    val defaultGroupName: String? = null,
) {
    val ids: List<String>
        get() = list.mapNotNull { it.id }
}