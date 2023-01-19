package com.twofasapp.persistence.cipher

import java.math.BigInteger
import java.security.SecureRandom

class DatabaseKeyGeneratorRandom : DatabaseKeyGenerator {

    override fun generate(bytes: Int): String {
        val secureRandom = SecureRandom()
        val token = ByteArray(bytes)
        secureRandom.nextBytes(token)

        return BigInteger(1, token).toString(16)
    }
}