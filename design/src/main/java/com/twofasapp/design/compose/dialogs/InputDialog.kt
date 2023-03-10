package com.twofasapp.design.compose.dialogs

import TextFieldOutlined
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.twofasapp.design.compose.dialogs.internal.BaseDialogButtons
import com.twofasapp.design.compose.dialogs.internal.BaseDialogContent
import com.twofasapp.design.compose.dialogs.internal.BaseDialogSurface
import com.twofasapp.design.compose.dialogs.internal.BaseDialogTextContent
import com.twofasapp.design.compose.dialogs.internal.BaseDialogTitle
import com.twofasapp.designsystem.TwTheme
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
                                                context.getString(com.twofasapp.resources.R.string.errors__input_integer_number)
                                            }
                                        } catch (e: Exception) {
                                            context.getString(com.twofasapp.resources.R.string.errors__input_integer_number)
                                        }
                                    }

                                    InputType.NumberDecimal -> {
                                        try {
                                            inputText.toFloat()
                                            null
                                        } catch (e: Exception) {
                                            context.getString(com.twofasapp.resources.R.string.errors__input_number)
                                        }
                                    }

                                    InputType.Password -> null
                                }
                                val validationResult = validation.invoke(inputText)

                                isPositiveEnabled = isInCharactersLimit && validationResult == Validation.Ok && invalidInputTypeError == null

                                validationErrorText = when {
                                    isInCharactersLimit.not() && inputText.length > (maxLength
                                        ?: 999999) -> context.getString(com.twofasapp.resources.R.string.errors__input_integer_number)
                                        .format(maxLength)

                                    isInCharactersLimit.not() && inputText.isEmpty() -> context.getString(com.twofasapp.resources.R.string.errors__input_empty)
                                    invalidInputTypeError != null -> invalidInputTypeError
                                    validationResult is Validation.Error -> validationResult.msg
                                        ?: validationResult.msgRes?.let { context.getString(it) }

                                    else -> null
                                }

                            },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TwTheme.color.onSurfacePrimary),
                            label = { if (hint != null) Text(text = hint) },
                            enabled = isEnabled,
                            error = validationErrorText ?: errorText ?: "",
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                disabledTextColor = TwTheme.color.onSurfaceSecondary,
                                focusedBorderColor = TwTheme.color.onSurfaceSecondary,
                                unfocusedBorderColor = TwTheme.color.onSurfaceSecondary,
                                focusedLabelColor = TwTheme.color.onSurfaceSecondary,
                                unfocusedLabelColor = TwTheme.color.onSurfaceSecondary,
                                errorLabelColor = TwTheme.color.error,
                                errorCursorColor = TwTheme.color.error,
                                errorBorderColor = TwTheme.color.error,
                                errorLeadingIconColor = TwTheme.color.error,
                                errorTrailingIconColor = TwTheme.color.error,
                                errorSupportingTextColor = TwTheme.color.error,
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