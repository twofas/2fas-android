package com.twofasapp.common.crypto

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Pbkdf {
    fun generate(
        input: String,
        salt: ByteArray,
        iterations: Int,
        keyLength: Int,
    ): ByteArray {
        val spec = PBEKeySpec(input.toCharArray(), salt, iterations, keyLength)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }
}