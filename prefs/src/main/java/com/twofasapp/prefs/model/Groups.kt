package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Groups(
    @SerialName("list")
    val list: MutableList<Group> = mutableListOf(),
    @SerialName("isDefaultGroupExpanded")
    val isDefaultGroupExpanded: Boolean = true
) {
    fun getById(id: String?): Group? =
        list.firstOrNull { it.id == id }
}