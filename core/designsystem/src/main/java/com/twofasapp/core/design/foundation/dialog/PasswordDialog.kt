package com.twofasapp.core.design.foundation.dialog

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.twofasapp.core.design.foundation.textfield.SecretField
import com.twofasapp.core.design.foundation.textfield.SecretFieldTrailingIcon
import com.twofasapp.core.design.foundation.textfield.TextField
import com.twofasapp.core.design.theme.DialogPadding
import com.twofasapp.locale.MdtLocale
import kotlinx.coroutines.android.awaitFrame

@Composable
fun PasswordDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    body: String? = null,
    bodyAnnotated: AnnotatedString? = null,
    error: String? = null,
    enabled: Boolean = true,
    positive: String? = MdtLocale.strings.commonSave,
    negative: String? = MdtLocale.strings.commonCancel,
    onBodyClick: ((Int) -> Unit)? = null,
    onPositive: ((String) -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    validation: ((String) -> Boolean)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    minLength: Int = 3,
    maxLength: Int = Int.MAX_VALUE,
    confirmRequired: Boolean = true,
    properties: DialogProperties = DialogProperties(),
) {
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordConfirmVisible by remember { mutableStateOf(false) }

    val positiveEnabledState by remember {
        derivedStateOf {
            when {
                password.trim().length !in minLength..maxLength -> false
                confirmRequired && password != passwordConfirm -> false
                else -> validation?.invoke(password) ?: true
            }
        }
    }
    val focusRequester = remember { FocusRequester() }

    BaseDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        body = body,
        bodyAnnotated = bodyAnnotated,
        positive = positive,
        negative = negative,
        onBodyClick = onBodyClick,
        onPositiveClick = { onPositive?.invoke(password.trim()) },
        onNegativeClick = onNegative,
        positiveEnabled = positiveEnabledState,
        negativeEnabled = true,
        properties = properties,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .padding(horizontal = DialogPadding)
                .focusRequester(focusRequester),
            labelText = MdtLocale.strings.password,
            isError = error.isNullOrBlank().not(),
            keyboardOptions = keyboardOptions.copy(
                keyboardType = KeyboardType.Password,
                capitalization = KeyboardCapitalization.None,
            ),
            maxLines = 1,
            enabled = enabled,
            supportingText = if (error.isNullOrBlank()) null else error,
            visualTransformation = VisualTransformation.SecretField(passwordVisible),
            trailingIcon = {
                SecretFieldTrailingIcon(
                    visible = passwordVisible,
                    onToggle = { passwordVisible = passwordVisible.not() },
                )
            },
        )

        if (confirmRequired) {
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = passwordConfirm,
                onValueChange = { passwordConfirm = it },
                modifier = Modifier
                    .padding(horizontal = DialogPadding),
                labelText = MdtLocale.strings.passwordConfirm,
                isError = error.isNullOrBlank().not(),
                keyboardOptions = keyboardOptions.copy(
                    keyboardType = KeyboardType.Password,
                    capitalization = KeyboardCapitalization.None,
                ),
                maxLines = 1,
                enabled = enabled,
                visualTransformation = VisualTransformation.SecretField(passwordConfirmVisible),
                trailingIcon = {
                    SecretFieldTrailingIcon(
                        visible = passwordConfirmVisible,
                        onToggle = { passwordConfirmVisible = passwordConfirmVisible.not() },
                    )
                },
            )
        }
    }

    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequester.requestFocus()
    }
}

val ExportPasswordRegex = Regex("([A-Za-z0-9_\\\\/!#\\\$%&\\+\\*~@\\?=^\\.,'\\(\\)\\{\\}\\[\\]:;<>\\|-]+)")

@Preview
@Composable
private fun Preview() {
    PasswordDialog(
        onDismissRequest = { },
        title = "Password",
        body = MdtLocale.strings.placeholderMedium,
    )
}