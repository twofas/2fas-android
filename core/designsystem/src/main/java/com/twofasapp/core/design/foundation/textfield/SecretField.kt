/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.textfield

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.foundation.button.IconButton

// Adapted from reference: the optional PasswordColors colorized branch is omitted (no
// PasswordColors/passwordColorized in this project and no call site uses it).
fun VisualTransformation.Companion.SecretField(
    visible: Boolean,
): VisualTransformation {
    return if (visible) {
        None
    } else {
        PasswordVisualTransformation()
    }
}

@Composable
fun SecretFieldTrailingIcon(
    visible: Boolean,
    onToggle: () -> Unit = {},
    testTag: String? = "revealSecretButton",
) {
    IconButton(
        // Reference uses Visibility/VisibilityOff; this project ships Eye/EyeSlash.
        icon = if (visible) MdtIcons.EyeSlash else MdtIcons.Eye,
        onClick = onToggle,
        modifier = if (testTag != null) Modifier.testTag(testTag) else Modifier,
    )
}