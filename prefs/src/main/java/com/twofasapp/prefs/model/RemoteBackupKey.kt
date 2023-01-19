package com.twofasapp.prefs.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteBackupKey(
    @SerialName("saltEncoded")
    val saltEncoded: String,
    @SerialName("keyEncoded")
    val keyEncoded: String,
)

fun RemoteBackupKey?.isSet(): Boolean = this != null && saltEncoded.isNotBlank() && keyEncoded.isNotBlank()
