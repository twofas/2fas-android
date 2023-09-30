package com.twofasapp.feature.security.biometric

import javax.crypto.SecretKey

interface BiometricKeyProvider {
    companion object {
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }

    fun getSecretKey(): SecretKey
    fun deleteSecretKey()
}