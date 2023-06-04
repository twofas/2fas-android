package com.twofasapp.design.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar

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
