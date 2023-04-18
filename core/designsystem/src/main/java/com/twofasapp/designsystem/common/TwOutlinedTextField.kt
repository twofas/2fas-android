package com.twofasapp.designsystem.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.semantics.fieldError

@Composable
fun textFieldsColors() = outlinedTextFieldColors(
    unfocusedLabelColor = TwTheme.color.onSurfaceSecondary,
    errorCursorColor = TwTheme.color.error,
    errorLabelColor = TwTheme.color.error,
    errorLeadingIconColor = TwTheme.color.error,
    errorSupportingTextColor = TwTheme.color.error,
    errorTrailingIconColor = TwTheme.color.error,
)

@Composable
fun TwOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    labelText: String? = null,
    supportingText: String? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    showCounter: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    maxLines: Int = Int.MAX_VALUE,
    maxLength: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.outlinedShape,
    colors: TextFieldColors = textFieldsColors(),
) {
    var textValue by remember { mutableStateOf(TextFieldValue(value, selection = TextRange(value.length))) }

    OutlinedTextField(
        value = textValue,
        onValueChange = {
            if (it.text.length <= maxLength) {
                textValue = it
                onValueChange.invoke(it.text)
            }
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = labelText?.let { { Text(text = it) } },
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = if (showCounter || supportingText != null) {
            {
                Text(
                    text = if (showCounter) {
                        "${value.length}/$maxLength"
                    } else {
                        supportingText.orEmpty()
                    },
                    style = LocalTextStyle.current,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { fieldError = supportingText.orEmpty() },
                    textAlign = if (showCounter) {
                        TextAlign.End
                    } else {
                        TextAlign.Start
                    },
                )
            }
        } else {
            null
        },
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = maxLines,
        singleLine = maxLines == 1,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
    )
}
//
//@Composable
//fun MdtTextFieldPassword(
//    value: String,
//    onValueChange: (String) -> Unit,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
//    readOnly: Boolean = false,
//    labelString: String? = null,
//    supportingString: String? = null,
//    isError: Boolean = false,
//    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
//    keyboardActions: KeyboardActions = KeyboardActions.Default,
//    singleLine: Boolean = false,
//    maxLines: Int = Int.MAX_VALUE,
//    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
//    colors: TextFieldColors = midniteTextFieldsColors(),
//) {
//    var isPasswordVisible by remember { mutableStateOf(false) }
//
//    MdtTextField(
//        value = value,
//        onValueChange = onValueChange,
//        modifier = modifier,
//        enabled = enabled,
//        readOnly = readOnly,
//        labelString = labelString,
//        supportingString = supportingString,
//        isError = isError,
//        keyboardOptions = keyboardOptions,
//        keyboardActions = keyboardActions,
//        singleLine = singleLine,
//        maxLines = maxLines,
//        interactionSource = interactionSource,
//        colors = colors,
//        visualTransformation = if (isPasswordVisible) {
//            VisualTransformation.None
//        } else {
//            PasswordVisualTransformation()
//        },
//        trailingIcon = {
//            IconButton(onClick = { isPasswordVisible = isPasswordVisible.not() }) {
//                Icon(
//                    painter = if (isPasswordVisible) {
//                        MdtIcons.EyeSlash
//                    } else {
//                        MdtIcons.Eye
//                    },
//                    contentDescription = null,
//                    tint = Carbon200,
//                )
//            }
//        },
//    )
//}
