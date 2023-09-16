package com.twofasapp.cipher.backup.internal

internal interface BackupSaltGenerator {
    fun generate(): ByteArray
}