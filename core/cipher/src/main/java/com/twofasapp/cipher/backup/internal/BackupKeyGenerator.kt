package com.twofasapp.cipher.backup.internal

import java.security.Key

internal interface BackupKeyGenerator {
    fun generate(password: String, salt: ByteArray): Key
}