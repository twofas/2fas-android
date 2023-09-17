package com.twofasapp.data.services.domain

data class BackupContentCreateResult(
    val backupContent: BackupContent,
    val keyEncoded: String? = null,
    val saltEncoded: String? = null,
)