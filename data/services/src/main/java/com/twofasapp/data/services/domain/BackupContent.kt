package com.twofasapp.data.services.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BackupContent(
    @SerialName("services")
    val services: List<BackupService> = emptyList(),
    @SerialName("groups")
    val groups: List<BackupGroup> = emptyList(),
    @SerialName("updatedAt")
    val updatedAt: Long = 0,
    @SerialName("schemaVersion")
    val schemaVersion: Int = CurrentSchema,
    @SerialName("appVersionCode")
    val appVersionCode: Int = 0,
    @SerialName("appVersionName")
    val appVersionName: String = "",
    @SerialName("appOrigin")
    val appOrigin: String = "android",
    @SerialName("account")
    val account: String? = null,
    @SerialName("servicesEncrypted")
    val servicesEncrypted: String? = null,
    @SerialName("reference")
    val reference: String? = null,
) {
    companion object {
        const val CurrentSchema = 3

        const val Reference =
            "tRViSsLKzd86Hprh4ceC2OP7xazn4rrt4xhfEUbOjxLX8Rc3mkISXE0lWbmnWfggogbBJhtYgpK6fMl1D6mtsy92R3HkdGfwuXbzLebqVFJsR7IZ2w58t938iymwG4824igYy1wi6n2WDpO1Q1P69zwJGs2F5a1qP4MyIiDSD7NCV2OvidXQCBnDlGfmz0f1BQySRkkt4ryiJeCjD2o4QsveJ9uDBUn8ELyOrESv5R5DMDkD4iAF8TXU7KyoJujd"

        val Empty
            get() = BackupContent()
    }
}