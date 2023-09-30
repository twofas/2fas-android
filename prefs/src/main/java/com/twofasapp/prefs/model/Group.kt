package com.twofasapp.prefs.model

import android.os.Parcelable
import com.twofasapp.common.domain.BackupSyncStatus
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Parcelize
data class Group(
    @SerialName("id")
    val id: String?,
    @SerialName("name")
    val name: String,
    @SerialName("isExpanded")
    val isExpanded: Boolean = true,
    @SerialName("updatedAt")
    val updatedAt: Long = 0,
    @SerialName("backupSyncStatus")
    val backupSyncStatus: BackupSyncStatus? = BackupSyncStatus.NOT_SYNCED,
) : Parcelable {

    companion object {
        fun generateNew(name: String) = Group(
            id = UUID.randomUUID().toString(),
            name = name,
        )
    }

    fun isContentEqualTo(group: Group) =
        name == group.name &&
                id == group.id &&
                isExpanded == group.isExpanded

    fun toRemote() = RemoteGroup(
        id = id!!,
        name = name,
        isExpanded = isExpanded,
        updatedAt = updatedAt
    )
}