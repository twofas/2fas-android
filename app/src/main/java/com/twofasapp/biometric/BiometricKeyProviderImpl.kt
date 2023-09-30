package com.twofasapp.biometric

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.twofasapp.feature.security.biometric.BiometricKeyProvider
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

internal class BiometricKeyProviderImpl : BiometricKeyProvider {

    companion object {
        private const val KeyAlias = "BiometricKey"
    }

    override fun getSecretKey(): SecretKey {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            if (keyStore.containsAlias(KeyAlias)) {
                return keyStore.getKey(KeyAlias, null) as SecretKey
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(KeyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    this.setInvalidatedByBiometricEnrollment(true)
                }
            }
            .build()

        keyGenerator.init(keyGenParameterSpec)

        return keyGenerator.generateKey()
    }

    override fun deleteSecretKey() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        keyStore.deleteEntry(KeyAlias)
    }
}