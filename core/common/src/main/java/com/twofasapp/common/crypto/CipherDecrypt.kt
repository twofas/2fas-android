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
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Cipher extension function to decrypt EncryptedBytes object.
 */
fun Cipher.decrypt(data: EncryptedBytes): ByteArray {
    return doFinal(data.data)
}

/**
 * Cipher object to decrypt data using AES-GCM.
 */
fun cipherDecrypt(key: Key, iv: ByteArray): Cipher {
    return Cipher.getInstance("AES/GCM/NoPadding").apply {
        init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
    }
}

fun decrypt(key: ByteArray, data: EncryptedBytes): ByteArray = decrypt(SecretKeySpec(key, "AES"), data)
fun decrypt(key: ByteArray, data: ByteArray): ByteArray = decrypt(key, EncryptedBytes(data))
fun decrypt(key: Key, data: EncryptedBytes): ByteArray {
    return cipherDecrypt(key = key, iv = data.iv).decrypt(data)
}