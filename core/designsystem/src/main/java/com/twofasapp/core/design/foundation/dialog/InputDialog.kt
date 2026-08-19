/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.dialog

import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.twofasapp.core.design.foundation.preview.PreviewTextLong
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.textfield.SecretField
import com.twofasapp.core.design.foundation.textfield.SecretFieldTrailingIcon
import com.twofasapp.core.design.foundation.textfield.TextField
import com.twofasapp.core.design.theme.DialogPadding
import com.twofasapp.locale.MdtLocale
import kotlinx.coroutines.android.awaitFrame

sealed interface InputValidation {
    data object Valid : InputValidation
    data class Invalid(val error: String?) : InputValidation
}

@Composable
fun InputDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    body: String? = null,
    bodyAnnotated: AnnotatedString? = null,
    label: String? = null,
    prefill: String? = null,
    validate: (String) -> InputValidation = { InputValidation.Valid },
    positive: String = MdtLocale.strings.commonSave,
    negative: String? = MdtLocale.strings.commonCancel,
    neutral: String? = null,
    icon: Painter? = null,
    iconColor: Color = Color.Unspecified,
    onPositive: (String) -> Unit = {},
    onNegative: () -> Unit = {},
    onNeutral: () -> Unit = {},
    positiveColor: Color = Color.Unspecified,
    negativeColor: Color = Color.Unspecified,
    neutralColor: Color = Color.Unspecified,
    actionsAlignment: ActionsAlignment = ActionsAlignment.Horizontal,
    properties: DialogProperties = DialogProperties(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isSecret: Boolean = false,
    excludeFromAutofill: Boolean = true,
) {
    var secretVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = prefill.orEmpty(),
                selection = TextRange(prefill.orEmpty().length),
            ),
        )
    }
    var startedTyping by remember { mutableStateOf(false) }
    val inputValidation by remember {
        derivedStateOf {
            if (startedTyping.not() && textFieldValue.text.isEmpty()) {
                InputValidation.Invalid(null)
            } else {
                validate(textFieldValue.text)
            }
        }
    }

    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequester.requestFocus()
    }

    BaseDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = title,
        body = body,
        bodyAnnotated = bodyAnnotated,
        positive = positive,
        negative = negative,
        neutral = neutral,
        icon = icon,
        iconColor = iconColor,
        onPositiveClick = { onPositive(textFieldValue.text) },
        onNegativeClick = onNegative,
        onNeutralClick = onNeutral,
        positiveColor = positiveColor,
        positiveEnabled = inputValidation is InputValidation.Valid,
        negativeColor = negativeColor,
        neutralColor = neutralColor,
        properties = properties,
        actionsAlignment = actionsAlignment,
        content = {
            if (excludeFromAutofill) {
                LocalView.current.rootView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
            }

            Column(
                modifier = Modifier.padding(horizontal = DialogPadding),
            ) {
                TextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    labelText = label.orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onKeyEvent {
                            startedTyping = true
                            false
                        },
                    singleLine = true,
                    maxLines = 1,
                    supportingText = (inputValidation as? InputValidation.Invalid)?.error.orEmpty(),
                    isError = startedTyping && inputValidation is InputValidation.Invalid,
                    keyboardOptions = keyboardOptions,
                    visualTransformation = if (isSecret) {
                        VisualTransformation.SecretField(secretVisible)
                    } else {
                        VisualTransformation.None
                    },
                    trailingIcon = if (isSecret) {
                        {
                            SecretFieldTrailingIcon(
                                visible = secretVisible,
                                onToggle = { secretVisible = !secretVisible },
                            )
                        }
                    } else {
                        null
                    },
                    keyboardActions = if (inputValidation is InputValidation.Valid) {
                        KeyboardActions(
                            onDone = { onPositive(textFieldValue.text) },
                        )
                    } else {
                        KeyboardActions.Default
                    },
                )
            }
        },
    )
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        InputDialog(
            onDismissRequest = { },
            title = "Input",
            body = PreviewTextLong,
        )
    }
}