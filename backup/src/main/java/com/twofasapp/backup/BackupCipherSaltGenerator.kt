package com.twofasapp.backup

interface BackupCipherSaltGenerator {
    fun generate(): ByteArray
}