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
import androidx.compose.ui.tooling.preview.Preview
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
    positiveEnabled: ((String) -> Boolean)? = null,
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

    val positiveEnabledState by remember {
        derivedStateOf {
            when {
                input.trim().length !in minLength..maxLength -> false
                else -> positiveEnabled?.invoke(input) ?: true
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
        positiveEnabled = positiveEnabledState,
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

    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
private fun Preview() {
    InputDialog(
        onDismissRequest = { },
        title = "Input",
    )
}