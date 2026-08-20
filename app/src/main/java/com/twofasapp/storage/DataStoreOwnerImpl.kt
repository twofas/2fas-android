/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.twofasapp.common.crypto.AndroidKeyStore
import com.twofasapp.common.storage.DataStoreOwner
import kotlinx.serialization.json.Json

internal class DataStoreOwnerImpl(
    override val dataStore: DataStore<Preferences>,
    override val androidKeyStore: AndroidKeyStore,
    override val json: Json,
) : DataStoreOwner