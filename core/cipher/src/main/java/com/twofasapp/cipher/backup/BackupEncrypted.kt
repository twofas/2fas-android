package com.twofasapp.cipher.backup

data class BackupEncrypted(
    val reference: DataEncrypted,
    val services: DataEncrypted,
    val keyEncoded: String,
    val saltEncoded: String,
)