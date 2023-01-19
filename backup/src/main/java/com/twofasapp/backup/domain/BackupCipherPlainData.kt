package com.twofasapp.backup.domain

data class BackupCipherPlainData(
    val reference: PlainData,
    val services: PlainData,
)