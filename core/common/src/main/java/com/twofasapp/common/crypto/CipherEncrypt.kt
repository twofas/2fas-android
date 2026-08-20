/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.crypto

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Cipher extension function to encrypt data to EncryptedBytes.
 */
fun Cipher.encrypt(data: ByteArray): EncryptedBytes {
    return EncryptedBytes(bytes = iv + doFinal(data))
}

/**
 * Cipher object to encrypt data using AES-GCM.
 */
fun cipherEncrypt(key: Key): Cipher {
    return Cipher.getInstance("AES/GCM/NoPadding").apply {
        init(Cipher.ENCRYPT_MODE, key)
    }
}

fun encrypt(key: ByteArray, data: String): EncryptedBytes = encrypt(key, data.toByteArray(Charsets.UTF_8))
fun encrypt(key: ByteArray, data: ByteArray): EncryptedBytes = encrypt(SecretKeySpec(key, "AES"), data)
fun encrypt(key: Key, data: String): EncryptedBytes = encrypt(key, data.toByteArray(Charsets.UTF_8))
fun encrypt(key: Key, data: ByteArray): EncryptedBytes {
    return cipherEncrypt(key = key).encrypt(data)
}