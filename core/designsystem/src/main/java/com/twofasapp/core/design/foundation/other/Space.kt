/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.other

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun ColumnScope.Space(height: Dp) {
    Spacer(Modifier.height(height))
}

@Composable
fun ColumnScope.Space(weight: Float) {
    Spacer(Modifier.weight(weight))
}

@Composable
fun RowScope.Space(width: Dp) {
    Spacer(Modifier.width(width))
}

@Composable
fun RowScope.Space(weight: Float) {
    Spacer(Modifier.weight(weight))
}