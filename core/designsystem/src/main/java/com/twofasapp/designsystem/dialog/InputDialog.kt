package com.twofasapp.designsystem.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.common.TwOutlinedTextField
import kotlinx.coroutines.android.awaitFrame

@Composable
fun InputDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    prefill: String? = null,
    hint: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    positive: String? = null,
    negative: String? = null,
    onPositiveClick: ((String) -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    showCounter: Boolean = false,
    minLength: Int = 0,
    maxLength: Int = Int.MAX_VALUE,
) {

    val context = LocalContext.current
    var validationErrorText by remember { mutableStateOf<String?>(null) }

    var input by remember { mutableStateOf(prefill.orEmpty()) }

    val positiveEnabled by remember {
        derivedStateOf {
            when {
                input.trim().length !in minLength..maxLength -> false
                else -> true
            }
        }
    }
    val focusRequester = remember { FocusRequester() }

    BaseDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        positive = positive,
        negative = negative,
        onPositiveClick = { onPositiveClick?.invoke(input.trim()) },
        onNegativeClick = onNegativeClick,
        positiveEnabled = positiveEnabled,
        negativeEnabled = true,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        TwOutlinedTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier
                .padding(horizontal = DialogPadding)
                .focusRequester(focusRequester),
            labelText = hint,
            showCounter = showCounter,
            isError = error.isNullOrBlank().not(),
            keyboardOptions = keyboardOptions,
            maxLines = 1,
            maxLength = maxLength,
            enabled = enabled,
        )
    }

//    Dialog(onDismissRequest = onDismiss) {
//        BaseDialogSurface {
//            Column {
//                BaseDialogTitle(title)
//                BaseDialogContent {
//                    Column {
//                        BaseDialogTextContent(text)
//                        if (text.isNullOrBlank().not()) {
//                            Spacer(modifier = Modifier.height(24.dp))
//                        }
//
//                        TextFieldOutlined(
//                            value = inputText,
//                            onValueChange = {
//                                inputText = it
//
//                                val minLength = if (allowEmpty) 0 else 1
//                                val isInCharactersLimit = inputText.length in minLength..(maxLength ?: 999999)
//                                val invalidInputTypeError: String? = when (inputType) {
//                                    InputType.Text -> null
//                                    InputType.NumberInteger -> {
//                                        try {
//                                            val num = inputText.toInt()
//
//                                            if (num >= 0) {
//                                                null
//                                            } else {
//                                                "Input must be integer number"
//                                            }
//                                        } catch (e: Exception) {
//                                            "Input must be integer number"
//                                        }
//                                    }
//
//                                    InputType.NumberDecimal -> {
//                                        try {
//                                            inputText.toFloat()
//                                            null
//                                        } catch (e: Exception) {
//                                            "Input must be a number"
//                                        }
//                                    }
//
//                                    InputType.Password -> null
//                                }
//                                val validationResult = validation.invoke(inputText)
//
//                                isPositiveEnabled = isInCharactersLimit && validationResult == Validation.Ok && invalidInputTypeError == null
//
//                                validationErrorText = when {
//                                    isInCharactersLimit.not() && inputText.length > (maxLength ?: 999999) -> "Input is too long. Limit: $maxLength"
//                                    isInCharactersLimit.not() && inputText.isEmpty() -> "Input can not be empty"
//                                    invalidInputTypeError != null -> invalidInputTypeError
//                                    validationResult is Validation.Error -> validationResult.msg
//                                        ?: validationResult.msgRes?.let { context.getString(it) }
//
//                                    else -> null
//                                }
//
//                            },
//                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TwTheme.color.onSurfacePrimary),
//                            label = { if (hint != null) Text(text = hint) },
//                            enabled = isEnabled,
//                            error = validationErrorText ?: errorText ?: "",
//                            colors = TextFieldDefaults.outlinedTextFieldColors(
//                                disabledTextColor = TwTheme.color.onSurfaceSecondary,
//                                focusedBorderColor = TwTheme.color.onSurfaceSecondary,
//                                unfocusedBorderColor = TwTheme.color.onSurfaceSecondary,
//                                focusedLabelColor = TwTheme.color.onSurfaceSecondary,
//                                unfocusedLabelColor = TwTheme.color.onSurfaceSecondary,
//                                errorLabelColor = TwTheme.color.error,
//                                errorCursorColor = TwTheme.color.error,
//                                errorBorderColor = TwTheme.color.error,
//                                errorLeadingIconColor = TwTheme.color.error,
//                                errorTrailingIconColor = TwTheme.color.error,
//                                errorSupportingTextColor = TwTheme.color.error,
//                            ),
//                            keyboardOptions = KeyboardOptions.Default.copy(
//                                keyboardType = when (inputType) {
//                                    InputType.Text -> KeyboardType.Text
//                                    InputType.NumberInteger -> KeyboardType.Number
//                                    InputType.NumberDecimal -> KeyboardType.Number
//                                    InputType.Password -> KeyboardType.Password
//                                }
//                            ),
//                            modifier = Modifier.focusRequester(focusRequester)
//                        )
//                    }
//                }
//                BaseDialogButtons(
//                    positiveText = positiveText,
//                    negativeText = negativeText,
//                    isPositiveEnabled = isPositiveEnabled,
//                    isNegativeEnabled = true,
//                    topMargin = false,
//                    onPositive = { onPositive?.invoke(inputText) },
//                    onNegative = { onNegative?.invoke() },
//                    onDismiss = { onDismiss.invoke() }
//                )
//            }
//        }
//    }

    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequester.requestFocus()
    }
}

sealed interface Validation {
    object Ok : Validation
    class Error(val msgRes: Int? = null, val msg: String? = null) : Validation
}