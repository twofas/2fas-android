package com.twofasapp.design.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.design.theme.textFieldHint
import com.twofasapp.design.theme.textPrimary

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    value: String = "",
    hint: String = "",
    showSearch: MutableState<Boolean>,
    onValueChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
) {
    val textValue = remember { mutableStateOf(value) }
    val showClearButton = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    if (showSearch.value) {
        OutlinedTextField(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    showClearButton.value = focusState.isFocused
                }
                .focusRequester(focusRequester),
            value = textValue.value,
            onValueChange = {
                textValue.value = it
                onValueChanged(it)
            },
            placeholder = {
                Text(text = hint, style = MaterialTheme.typography.body1.copy(fontSize = 18.sp))
            },
            textStyle = MaterialTheme.typography.body1.copy(fontSize = 18.sp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colors.textPrimary,
                disabledTextColor = Color.Black,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colors.textFieldHint,
                unfocusedLabelColor = MaterialTheme.colors.textFieldHint,
                errorLabelColor = MaterialTheme.colors.error,
            ),
            trailingIcon = {
                AnimatedVisibility(
                    visible = showClearButton.value,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = {
                        if (textValue.value.isNotEmpty()) {
                            textValue.value = ""
                            onValueChanged("")
                        } else {
                            showClearButton.value = false
                            showSearch.value = false
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }

                        onClearClick()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            tint = MaterialTheme.colors.textPrimary,
                            contentDescription = null
                        )
                    }
                }
            },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    } else {
        textValue.value = ""
        onValueChanged("")
    }
}