package com.twofasapp.backup

import java.security.SecureRandom

class BackupCipherSaltGeneratorRandom : BackupCipherSaltGenerator {

    override fun generate(): ByteArray {
        val secureRandom = SecureRandom()
        val salt = ByteArray(256)
        secureRandom.nextBytes(salt)
        return salt
    }
}