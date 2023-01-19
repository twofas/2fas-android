package com.twofasapp.backup

import android.os.Build
import java.security.Key
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class BackupCipherKeyGeneratorPbkdf : BackupCipherKeyGenerator {

    private companion object {
        private const val ITERATIONS = 10_000
        private const val KEY_LENGTH = 256
    }

    override fun generate(password: String, salt: ByteArray): Key {
        val pbKeySpec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)

        val keyBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            secretKeyFactory.generateSecret(pbKeySpec).encoded
        } else {
            PbkdfLegacy.pbkdf2("HmacSHA256", password.toByteArray(), salt, ITERATIONS, KEY_LENGTH / 8);
        }

        return SecretKeySpec(keyBytes, "AES")
    }
}