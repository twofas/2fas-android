package com.twofasapp.backup

import java.security.Key

interface BackupCipherKeyGenerator {
    fun generate(password: String, salt: ByteArray): Key
}