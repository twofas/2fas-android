/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.feature.developer.ui

import com.twofasapp.common.environment.AppBuild

internal data class DeveloperUiState(
    val appBuild: AppBuild? = null,
    val servicesCount: Int = 0,
    val trashedServicesCount: Int = 0,
    val supportedServicesCount: Int = 0,
)