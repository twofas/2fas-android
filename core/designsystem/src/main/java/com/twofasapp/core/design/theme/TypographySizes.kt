/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.theme

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

internal object TypographySizes {
    data class SizeSpec(
        val fontSize: TextUnit,
        val lineHeight: TextUnit,
    )

    val xs5 = SizeSpec(fontSize = 8.sp, lineHeight = 10.sp)
    val xs4 = SizeSpec(fontSize = 10.sp, lineHeight = 12.sp)
    val xs3 = SizeSpec(fontSize = 11.sp, lineHeight = 12.sp)
    val xs2 = SizeSpec(fontSize = 12.sp, lineHeight = 16.sp)
    val xs = SizeSpec(fontSize = 13.sp, lineHeight = 16.sp)
    val sm = SizeSpec(fontSize = 14.sp, lineHeight = 20.sp)
    val base = SizeSpec(fontSize = 16.sp, lineHeight = 24.sp)
    val lg = SizeSpec(fontSize = 18.sp, lineHeight = 28.sp)
    val xl = SizeSpec(fontSize = 20.sp, lineHeight = 28.sp)
    val xl2 = SizeSpec(fontSize = 24.sp, lineHeight = 32.sp)
    val xl3 = SizeSpec(fontSize = 28.sp, lineHeight = 34.sp)
    val xl4 = SizeSpec(fontSize = 36.sp, lineHeight = 40.sp)
}