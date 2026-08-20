/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.crypto

import java.security.SecureRandom

object RandomGenerator {
    /**
     * Generate secure random entropy
     * @param bytes length of entropy in bytes
     */
    fun generate(bytes: Int): ByteArray {
        val secureRandom = SecureRandom()
        val entropy = ByteArray(bytes)
        secureRandom.nextBytes(entropy)
        return entropy
    }
}