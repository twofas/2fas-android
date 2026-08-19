/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.core.design.AppTheme
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.preview.PreviewColumn
import com.twofasapp.core.design.theme.RoundedShape8

@Composable
fun TextField(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    labelText: String? = null,
    placeholderText: String? = null,
    label: @Composable (() -> Unit)? = labelText?.let { { Text(text = it) } },
    placeholder: @Composable (() -> Unit)? = placeholderText?.let { { Text(text = it) } },
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    supporting: @Composable (() -> Unit)? = supportingText?.let { { Text(text = it) } },
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = remember { MutableInteractionSource() },
    shape: Shape = RoundedShape8,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors().copy(
        unfocusedIndicatorColor = MdtTheme.color.outlineVariant,
    ),
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supporting,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
    )
}

@Composable
fun TextField(
    value: TextFieldValue,
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    labelText: String? = null,
    label: @Composable (() -> Unit)? = labelText?.let { { Text(text = it) } },
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    supporting: @Composable (() -> Unit)? = supportingText?.let { { Text(text = it) } },
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors().copy(
        unfocusedIndicatorColor = MdtTheme.color.outlineVariant,
    ),
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supporting,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
    )
}

object TextFieldDefaults {
    val shape: Shape = RoundedShape8
}

@Preview
@Composable
private fun PreviewsDark() {
    PreviewColumn(theme = AppTheme.Dark) { Previews() }
}

@Preview
@Composable
private fun PreviewsLight() {
    PreviewColumn(theme = AppTheme.Light) { Previews() }
}

@Composable
private fun Previews() {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = "",
        labelText = "Label",
    )

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = "Text value",
    )

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = "Text value",
        labelText = "Label",
    )

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = "Text value",
        labelText = "Label",
        enabled = false,
    )

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = "Text value",
        labelText = "Label",
        isError = true,
        supportingText = "This is error text",
    )

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = "Secret",
        labelText = "Password",
        visualTransformation = VisualTransformation.SecretField(false),
        trailingIcon = {
            SecretFieldTrailingIcon(
                visible = false,
            )
        },
    )
}