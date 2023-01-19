package com.twofasapp.design.compose.dialogs

import TextFieldOutlined
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.twofasapp.design.compose.dialogs.internal.*
import com.twofasapp.design.theme.textFieldDisabledText
import com.twofasapp.design.theme.textFieldHint
import com.twofasapp.design.theme.textFieldOutline
import com.twofasapp.design.theme.textPrimary
import kotlinx.coroutines.android.awaitFrame

@Composable
fun InputDialog(
    title: String? = null,
    text: String? = null,
    prefill: String? = null,
    hint: String? = null,
    positiveText: String? = null,
    negativeText: String? = null,
    errorText: String? = null,
    onPositive: ((String) -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    onDismiss: () -> Unit = {},
    inputType: InputType = InputType.Text,
    allowEmpty: Boolean = true,
    maxLength: Int? = null,
    validation: (text: String) -> Validation = { Validation.Ok },
    isEnabled: Boolean = true,
) {

    val context = LocalContext.current
    var inputText by remember { mutableStateOf(prefill.orEmpty()) }
    var validationErrorText by remember { mutableStateOf<String?>(null) }
    var isPositiveEnabled by remember { mutableStateOf(allowEmpty) }
    val focusRequester = remember { FocusRequester() }

    Dialog(onDismissRequest = onDismiss) {
        BaseDialogSurface {
            Column {
                BaseDialogTitle(title)
                BaseDialogContent {
                    Column {
                        BaseDialogTextContent(text)
                        if (text.isNullOrBlank().not()) {
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        TextFieldOutlined(
                            value = inputText,
                            onValueChange = {
                                inputText = it

                                val minLength = if (allowEmpty) 0 else 1
                                val isInCharactersLimit = inputText.length in minLength..(maxLength ?: 999999)
                                val invalidInputTypeError: String? = when (inputType) {
                                    InputType.Text -> null
                                    InputType.NumberInteger -> {
                                        try {
                                            val num = inputText.toInt()

                                            if (num >= 0) {
                                                null
                                            } else {
                                                "Input must be integer number"
                                            }
                                        } catch (e: Exception) {
                                            "Input must be integer number"
                                        }
                                    }
                                    InputType.NumberDecimal -> {
                                        try {
                                            inputText.toFloat()
                                            null
                                        } catch (e: Exception) {
                                            "Input must be a number"
                                        }
                                    }
                                    InputType.Password -> null
                                }
                                val validationResult = validation.invoke(inputText)

                                isPositiveEnabled = isInCharactersLimit && validationResult == Validation.Ok && invalidInputTypeError == null

                                validationErrorText = when {
                                    isInCharactersLimit.not() && inputText.length > (maxLength ?: 999999) -> "Input is too long. Limit: $maxLength"
                                    isInCharactersLimit.not() && inputText.isEmpty() -> "Input can not be empty"
                                    invalidInputTypeError != null -> invalidInputTypeError
                                    validationResult is Validation.Error -> validationResult.msg
                                        ?: validationResult.msgRes?.let { context.getString(it) }
                                    else -> null
                                }

                            },
                            textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.textPrimary),
                            label = { if (hint != null) Text(text = hint) },
                            enabled = isEnabled,
                            error = validationErrorText ?: errorText ?: "",
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                disabledTextColor = MaterialTheme.colors.textFieldDisabledText,
                                focusedBorderColor = MaterialTheme.colors.textFieldOutline,
                                unfocusedBorderColor = MaterialTheme.colors.textFieldOutline,
                                focusedLabelColor = MaterialTheme.colors.textFieldHint,
                                unfocusedLabelColor = MaterialTheme.colors.textFieldHint,
                                errorLabelColor = MaterialTheme.colors.error,
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = when (inputType) {
                                    InputType.Text -> KeyboardType.Text
                                    InputType.NumberInteger -> KeyboardType.Number
                                    InputType.NumberDecimal -> KeyboardType.Number
                                    InputType.Password -> KeyboardType.Password
                                }
                            ),
                            modifier = Modifier.focusRequester(focusRequester)
                        )
                    }
                }
                BaseDialogButtons(
                    positiveText = positiveText,
                    negativeText = negativeText,
                    isPositiveEnabled = isPositiveEnabled,
                    isNegativeEnabled = true,
                    topMargin = false,
                    onPositive = { onPositive?.invoke(inputText) },
                    onNegative = { onNegative?.invoke() },
                    onDismiss = { onDismiss.invoke() }
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequester.requestFocus()
    }
}

sealed interface Validation {
    object Ok : Validation
    class Error(val msgRes: Int? = null, val msg: String? = null) : Validation
}

enum class InputType {
    Text, NumberInteger, NumberDecimal, Password
}