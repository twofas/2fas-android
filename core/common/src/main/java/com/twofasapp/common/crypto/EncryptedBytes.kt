/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.crypto

/**
 * Represents a container for encrypted data, including the initialization vector (IV).
 *
 * This class encapsulates a byte array that holds both the IV and the encrypted data.
 * It provides convenient methods to access the IV and the encrypted data separately.
 *
 * @property bytes The combined byte array containing both the IV and the encrypted data.
 * The first [ivSizeBytes] bytes represent the IV, and the remaining bytes represent the encrypted data.
 */
data class EncryptedBytes(val bytes: ByteArray) {
    companion object {
        private const val ivSizeBytes = 12
    }

    constructor(
        iv: ByteArray,
        data: ByteArray,
    ) : this(iv + data)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedBytes

        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int = bytes.contentHashCode()

    /**
     * The initialization vector (IV) used for encryption/decryption.
     * This property returns a copy of the first `ivSizeBytes` bytes from the underlying `bytes` array.
     */
    val iv: ByteArray
        get() = bytes.copyOf(ivSizeBytes)

    /**
     * The decrypted data.
     * This property returns a copy of the underlying byte array, excluding the initialization vector (IV).
     * The IV is used during the decryption process but is not considered part of the actual data.
     */
    val data: ByteArray
        get() = bytes.copyOfRange(ivSizeBytes, bytes.size)
}

fun emptyEncryptedBytes() = EncryptedBytes(byteArrayOf())