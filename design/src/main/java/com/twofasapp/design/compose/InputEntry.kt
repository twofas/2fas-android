package com.twofasapp.design.compose

import TextFieldOutlined
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.twofasapp.design.R
import com.twofasapp.design.compose.dialogs.InputType
import com.twofasapp.design.compose.dialogs.Validation
import com.twofasapp.design.theme.textFieldDisabledText
import com.twofasapp.design.theme.textFieldHint
import com.twofasapp.design.theme.textFieldOutline

@Composable
fun InputEntry(
    text: String = "",
    hint: String = "",
    icon: Painter? = null,
    iconTint: Color = Color.Unspecified,
    iconVisibleWhenNotSet: Boolean = true,
    isEnabled: Boolean = true,
    errorText: String? = null,
    maxChars: Int = 999999,
    allowEmpty: Boolean = true,
    inputType: InputType = InputType.Text,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    validation: (text: String) -> Validation = { Validation.Ok },
    onValueChange: (isValid: Boolean, String) -> Unit = { _, _ -> },
    click: (() -> Unit)? = null,
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(56.dp, Dp.Infinity)
            .clickable(isEnabled) { click?.invoke() }
            .padding(
                horizontal = 16.dp,
            )
            .padding(top = 8.dp, bottom = 4.dp)
    ) {

        val context = LocalContext.current
        val (iconRef, inputRef) = createRefs()
        val alpha = if (isEnabled) 1f else 0.3f
        var validationErrorText by remember { mutableStateOf<String?>(null) }
        var isValid by remember { mutableStateOf(allowEmpty) }

        Icon(
            painter = icon ?: painterResource(com.twofasapp.resources.R.drawable.ic_placeholder),
            contentDescription = null,
            tint = if (iconTint != Color.Unspecified) iconTint else MaterialTheme.colors.primary,
            modifier = Modifier
                .size(if (iconVisibleWhenNotSet) 24.dp else 0.dp)
                .alpha(if (icon == null) 0f else alpha)
                .constrainAs(iconRef) {
                    top.linkTo(inputRef.top)
                    bottom.linkTo(inputRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 4.dp)
                    end.linkTo(inputRef.start)
                }
        )

        TextFieldOutlined(
            value = text,
            label = { Text(text = hint) },
            maxChars = maxChars,
            onValueChange = { inputText ->
                val minLength = if (allowEmpty) 0 else 1
                val isInCharactersLimit = inputText.length in minLength..maxChars
                val invalidInputTypeError: String? = when (inputType) {
                    InputType.Text -> null
                    InputType.Password -> null
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
                }
                val validationResult = validation.invoke(inputText)

                isValid = isInCharactersLimit && validationResult == Validation.Ok && invalidInputTypeError == null

                validationErrorText = when {
                    isInCharactersLimit.not() && inputText.length > maxChars -> "Input is too long. Limit: $maxChars"
                    isInCharactersLimit.not() && inputText.isEmpty() -> "Input can not be empty"
                    invalidInputTypeError != null -> invalidInputTypeError
                    validationResult is Validation.Error -> validationResult.msg
                        ?: validationResult.msgRes?.let { context.getString(it) }
                    else -> null
                }

                onValueChange.invoke(isValid, inputText)
            },
            error = validationErrorText ?: errorText ?: "",
            enabled = isEnabled,
            readOnly = readOnly,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = MaterialTheme.colors.textFieldDisabledText,
                focusedBorderColor = MaterialTheme.colors.textFieldOutline,
                unfocusedBorderColor = MaterialTheme.colors.textFieldOutline,
                focusedLabelColor = MaterialTheme.colors.textFieldHint,
                unfocusedLabelColor = MaterialTheme.colors.textFieldHint,
                errorLabelColor = MaterialTheme.colors.error,
            ),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions.copy(
                keyboardType = when (inputType) {
                    InputType.Text -> KeyboardType.Text
                    InputType.NumberInteger -> KeyboardType.Number
                    InputType.NumberDecimal -> KeyboardType.Number
                    InputType.Password -> KeyboardType.Password
                }
            ),
            trailingIcon = trailingIcon,
            modifierColumn = Modifier
                .alpha(alpha)
                .padding(0.dp)
                .constrainAs(inputRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(iconRef.end, margin = if (iconVisibleWhenNotSet) 28.dp else 16.dp)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .run {
                    if (focusRequester != null) {
                        this.focusRequester(focusRequester)
                    } else {
                        this
                    }
                }

        )
    }
}

@Preview
@Composable
internal fun PreviewInputEntryItem() {
    InputEntry(
        text = "Title",
        icon = painterResource(id = com.twofasapp.resources.R.drawable.ic_send_feedback),
        click = {}
    )
}