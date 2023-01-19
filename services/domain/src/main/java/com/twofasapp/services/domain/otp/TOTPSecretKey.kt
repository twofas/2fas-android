package com.twofasapp.services.domain.otp

import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Base64
import java.util.Arrays

/**
 * OTP authenticator key.
 */
data class TOTPSecretKey(val value: ByteArray) {

    enum class KeyRepresentation { BASE32, BASE64 }

    fun to(representation: KeyRepresentation): String {
        when (representation) {
            KeyRepresentation.BASE32 -> return Base32().encodeToString(value)
            KeyRepresentation.BASE64 -> return Base64().encodeToString(value)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as TOTPSecretKey
        if (!Arrays.equals(value, other.value)) return false
        return true
    }

    override fun hashCode(): Int = Arrays.hashCode(value)

    companion object {
        fun from(representation: KeyRepresentation, value: String): TOTPSecretKey {
            when (representation) {
                KeyRepresentation.BASE32 -> return TOTPSecretKey(Base32().decode(value))
                KeyRepresentation.BASE64 -> return TOTPSecretKey(Base64().decode(value))
            }
        }
    }
}