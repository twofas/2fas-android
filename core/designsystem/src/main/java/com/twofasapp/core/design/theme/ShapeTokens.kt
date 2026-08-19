/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

val RoundedShape2 = RoundedCornerShape(DimenTokens.radius_xs2)
val RoundedShape4 = RoundedCornerShape(DimenTokens.radius_xs)
val RoundedShape8 = RoundedCornerShape(DimenTokens.radius_sm)
val RoundedShape12 = RoundedCornerShape(DimenTokens.radius_md)
val RoundedShape16 = RoundedCornerShape(DimenTokens.radius_lg)
val RoundedShape24 = RoundedCornerShape(DimenTokens.radius_xl)
val RoundedShape32 = RoundedCornerShape(DimenTokens.radius_xl2)
val RoundedShape40 = RoundedCornerShape(DimenTokens.radius_xl3)

val RoundedTopShape = RoundedCornerShape(
    topStart = 12.dp,
    topEnd = 12.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp,
)

val RoundedBottomShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 0.dp,
    bottomStart = 12.dp,
    bottomEnd = 12.dp,
)

val DialogShape = RoundedCornerShape(24.dp)

val ButtonShape = RoundedCornerShape(12.dp)

fun RoundedShapeIndexed(isFirst: Boolean, isLast: Boolean): Shape {
    return if (isFirst && isLast) {
        RoundedShape12
    } else if (isFirst) {
        RoundedTopShape
    } else if (isLast) {
        RoundedBottomShape
    } else {
        RectangleShape
    }
}