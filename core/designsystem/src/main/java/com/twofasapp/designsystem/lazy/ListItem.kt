package com.twofasapp.designsystem.lazy

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

interface ListItem {
    val key: Any
    val type: Any
}

fun LazyListScope.listItem(
    type: ListItem,
    content: @Composable LazyItemScope.() -> Unit
) {
    item(
        key = type.key,
        contentType = type.type,
        content = content
    )
}

fun <T> LazyListScope.listItems(
    items: List<T>,
    type: ((item: T) -> ListItem),
    itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
    items(
        count = items.size,
        key = { index: Int -> type(items[index]).key },
        contentType = { index: Int -> type(items[index]).type },
    ) {
        itemContent(items[it])
    }
}