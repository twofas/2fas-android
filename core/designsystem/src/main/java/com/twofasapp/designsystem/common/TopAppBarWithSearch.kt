package com.twofasapp.designsystem.common

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme

@Composable
fun TopAppBarWithSearch(
    title: String,
    searchHint: String = "",
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    onSearchValueChanged: (String) -> Unit = {},
    navigationClick: () -> Unit
) {
    val showSearch = remember { mutableStateOf(false) }

    TwTopAppBar(title = {
        if (showSearch.value) {
            SearchBar(hint = searchHint, showSearch = showSearch, onValueChanged = onSearchValueChanged)
        } else {
            Text(text = title, color = TwTheme.color.onSurfacePrimary)
        }
    }, navigationIcon = {
        IconButton(onClick = {
            if (showSearch.value) {
                showSearch.value = false
                onSearchValueChanged("")
            } else {
                navigationClick.invoke()
            }
        }) {
            Icon(TwIcons.ArrowBack, null, tint = TwTheme.color.onSurfacePrimary)
        }
    }, actions = {
        if (showSearch.value.not()) {
            IconButton(onClick = { showSearch.value = true }) {
                Icon(TwIcons.Search, null, tint = TwTheme.color.primary)
            }
        }
        actions()
    }, modifier = modifier
    )
}

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

    BackHandler(
        enabled = showSearch.value
    ) {
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
    }

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
                Text(text = hint, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TwTheme.color.onSurfacePrimary,
                disabledTextColor = Color.Black,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedLabelColor = TwTheme.color.onSurfaceSecondary,
                unfocusedLabelColor = TwTheme.color.onSurfaceSecondary,
                errorLabelColor = TwTheme.color.error,
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
                            painter = TwIcons.Close,
                            tint = TwTheme.color.onSurfacePrimary,
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