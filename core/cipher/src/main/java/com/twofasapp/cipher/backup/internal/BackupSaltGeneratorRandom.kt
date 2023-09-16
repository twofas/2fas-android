package com.twofasapp.cipher.backup.internal

import java.security.SecureRandom

internal class BackupSaltGeneratorRandom : BackupSaltGenerator {

    override fun generate(): ByteArray {
        val secureRandom = SecureRandom()
        val salt = ByteArray(256)
        secureRandom.nextBytes(salt)
        return salt
    }
}