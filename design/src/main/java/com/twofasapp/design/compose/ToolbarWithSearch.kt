package com.twofasapp.design.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.twofasapp.design.theme.textPrimary
import com.twofasapp.design.theme.toolbar
import com.twofasapp.design.theme.toolbarContent


@Composable
fun ToolbarWithSearch(
    title: String,
    searchHint: String = "",
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    onSearchValueChanged: (String) -> Unit = {},
    navigationClick: () -> Unit
) {
    val showSearch = remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = title, color = MaterialTheme.colors.textPrimary) },
        navigationIcon = {
            IconButton(onClick = {
                if (showSearch.value) {
                    showSearch.value = false
                } else {
                    navigationClick.invoke()
                }
            }) {
                Icon(Icons.Filled.ArrowBack, null, tint = MaterialTheme.colors.toolbarContent)
            }
        },
        actions = {
            SearchBar(hint = searchHint, showSearch = showSearch, onValueChanged = onSearchValueChanged)
            IconButton(onClick = { showSearch.value = true }) {
                Icon(Icons.Filled.Search, null, tint = MaterialTheme.colors.primary)
            }
            actions()
        },
        backgroundColor = MaterialTheme.colors.toolbar,
        contentColor = MaterialTheme.colors.toolbarContent,
        modifier = modifier
    )
}
