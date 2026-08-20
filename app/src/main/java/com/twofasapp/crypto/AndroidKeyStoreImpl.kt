/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.twofasapp.common.crypto.AndroidKeyStore
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator

internal class AndroidKeyStoreImpl : AndroidKeyStore {
    companion object {
        private const val keyStoreProvider = "AndroidKeyStore"
        private const val keyPurposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        private const val keyAlgorithm = KeyProperties.KEY_ALGORITHM_AES
        private const val keyBlockMode = KeyProperties.BLOCK_MODE_GCM
        private const val keyPadding = KeyProperties.ENCRYPTION_PADDING_NONE

        private const val appKeyAlias = "twofasapp_app_key"
    }

    private val keyStore: KeyStore
        get() = KeyStore.getInstance(keyStoreProvider).also { it.load(null) }

    override val appKey: Key
        get() {
            if (keyStore.containsAlias(appKeyAlias)) {
                return keyStore.getKey(appKeyAlias, null)
            }

            return KeyGenerator.getInstance(keyAlgorithm, keyStoreProvider).run {
                init(
                    KeyGenParameterSpec
                        .Builder(appKeyAlias, keyPurposes)
                        .setBlockModes(keyBlockMode)
                        .setEncryptionPaddings(keyPadding)
                        .setKeySize(256)
                        .build(),
                )

                generateKey()
            }
        }
}