/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.common.crypto

import java.util.UUID

object Uuid {
    /**
     * Generate random uuid
     */
    fun generate(): String {
        return UUID.randomUUID().toString()
    }
}