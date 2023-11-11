package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteBackup(
    @SerialName("services")
    val services: List<RemoteService> = emptyList(),
    @SerialName("updatedAt")
    val updatedAt: Long = 0,
    @SerialName("schemaVersion")
    val schemaVersion: Int = CURRENT_SCHEMA,
    @SerialName("appVersionCode")
    val appVersionCode: Int = 0,
    @SerialName("appVersionName")
    val appVersionName: String = "",
    @SerialName("appOrigin")
    val appOrigin: String = "android",
    @SerialName("groups")
    val groups: List<RemoteGroup> = emptyList(),
    @SerialName("account")
    val account: String? = null,
    @SerialName("servicesEncrypted")
    val servicesEncrypted: String? = null,
    @SerialName("reference")
    val reference: String? = null,
) {
    companion object {
        const val CURRENT_SCHEMA = 4
    }
}