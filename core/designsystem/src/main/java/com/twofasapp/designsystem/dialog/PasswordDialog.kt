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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.twofasapp.designsystem.common.TwOutlinedTextFieldPassword
import com.twofasapp.locale.TwLocale
import kotlinx.coroutines.android.awaitFrame

@Composable
fun PasswordDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    body: String? = null,
    bodyAnnotated: AnnotatedString? = null,
    error: String? = null,
    enabled: Boolean = true,
    positive: String? = TwLocale.strings.commonSave,
    negative: String? = TwLocale.strings.commonCancel,
    onBodyClick: ((Int) -> Unit)? = null,
    onPositive: ((String) -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    validation: ((String) -> Boolean)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Password,
    ),
    minLength: Int = 3,
    maxLength: Int = Int.MAX_VALUE,
    confirmRequired: Boolean = true,
    properties: DialogProperties = DialogProperties(),
) {
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

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

        TwOutlinedTextFieldPassword(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .padding(horizontal = DialogPadding)
                .focusRequester(focusRequester),
            labelText = TwLocale.strings.password,
            isError = error.isNullOrBlank().not(),
            keyboardOptions = keyboardOptions,
            maxLines = 1,
            enabled = enabled,
            supportingText = if (error.isNullOrBlank()) null else error,
        )

        if (confirmRequired) {
            Spacer(modifier = Modifier.height(16.dp))
            TwOutlinedTextFieldPassword(
                value = passwordConfirm,
                onValueChange = { passwordConfirm = it },
                modifier = Modifier
                    .padding(horizontal = DialogPadding),
                labelText = TwLocale.strings.passwordConfirm,
                isError = error.isNullOrBlank().not(),
                keyboardOptions = keyboardOptions,
                maxLines = 1,
                enabled = enabled,
            )
        }

    }

    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequester.requestFocus()
    }
}


@Preview
@Composable
private fun Preview() {
    PasswordDialog(
        onDismissRequest = { },
        title = "Password",
        body = TwLocale.strings.placeholderMedium,
    )
}