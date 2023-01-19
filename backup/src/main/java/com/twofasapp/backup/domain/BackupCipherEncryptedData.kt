package com.twofasapp.backup.domain

data class BackupCipherEncryptedData(
    val reference: EncryptedData,
    val services: EncryptedData,
)