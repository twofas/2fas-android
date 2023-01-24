package com.twofasapp.data.services.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "secret") val secret: String,
    @ColumnInfo(name = "serviceTypeId") val serviceTypeId: String?,
    @ColumnInfo(name = "iconCollectionId") val iconCollectionId: String?,
    @ColumnInfo(name = "source") val source: String?,

    @ColumnInfo(name = "otpLink") val otpLink: String?,
    @ColumnInfo(name = "otpLabel") val otpLabel: String?,
    @ColumnInfo(name = "otpAccount") val otpAccount: String?,
    @ColumnInfo(name = "otpIssuer") val otpIssuer: String?,
    @ColumnInfo(name = "otpDigits") val otpDigits: Int?,
    @ColumnInfo(name = "otpPeriod") val otpPeriod: Int?,
    @ColumnInfo(name = "otpAlgorithm") val otpAlgorithm: String?,

    @ColumnInfo(name = "backupSyncStatus") val backupSyncStatus: String,
    @ColumnInfo(name = "updatedAt") val updatedAt: Long,
    @ColumnInfo(name = "badgeColor") val badgeColor: String?,
    @ColumnInfo(name = "selectedImageType") val selectedImageType: String?,
    @ColumnInfo(name = "labelText") val labelText: String?,
    @ColumnInfo(name = "labelBackgroundColor") val labelBackgroundColor: String?,
    @ColumnInfo(name = "groupId") val groupId: String?,
    @ColumnInfo(name = "isDeleted") val isDeleted: Boolean?,
    @ColumnInfo(name = "authType") val authType: String?,
    @ColumnInfo(name = "hotpCounter") val hotpCounter: Int?,
    @ColumnInfo(name = "assignedDomains") val assignedDomains: List<String>?,
)