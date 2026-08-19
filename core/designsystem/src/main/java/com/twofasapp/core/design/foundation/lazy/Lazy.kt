/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.lazy

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

fun LazyListScope.listItem(
    type: ListItem,
    content: @Composable LazyItemScope.() -> Unit,
) {
    item(key = type.key, contentType = type.type, content = content)
}

fun <T> LazyListScope.listItems(
    items: List<T>,
    type: ((item: T) -> ListItem),
    itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) {
    items(
        count = items.size,
        key = { index: Int -> type(items[index]).key },
        contentType = { index: Int -> type(items[index]).type },
    ) {
        itemContent(items[it])
    }
}

fun LazyListScope.stickyListItem(
    type: ListItem,
    content: @Composable LazyItemScope.(Int) -> Unit,
) {
    stickyHeader(key = type.key, contentType = type.type, content = content)
}

inline fun <T> Iterable<T>.forEachIndexed(action: (index: Int, isFirst: Boolean, isLast: Boolean, item: T) -> Unit) {
    forEachIndexed { index, item ->
        action(index, index == 0, index == (this as Collection).size - 1, item)
    }
}

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    var lastScrollDirection by remember(this) { mutableStateOf(true) }

    return remember(this) {
        derivedStateOf {
            // If list is not scrollable (content fits in viewport), always return true to show bottom bar
            val canScroll = canScrollBackward || canScrollForward
            if (!canScroll) {
                return@derivedStateOf true
            }

            // Edge case: at the top of the list, always consider scrolling up
            if (firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0) {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
                lastScrollDirection = true
                return@derivedStateOf true
            }

            // Only update direction when actively scrolling (prevents bounce-back from affecting state)
            if (isScrollInProgress) {
                val scrollingUp = if (previousIndex != firstVisibleItemIndex) {
                    previousIndex > firstVisibleItemIndex
                } else {
                    previousScrollOffset >= firstVisibleItemScrollOffset
                }

                // If we're at the bottom and detecting upward scroll, it's likely overscroll bounce
                // Keep the last direction instead of switching
                val atBottom = !canScrollForward
                val shouldIgnore = atBottom && scrollingUp && !lastScrollDirection

                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset

                if (!shouldIgnore) {
                    lastScrollDirection = scrollingUp
                }

                if (shouldIgnore) lastScrollDirection else scrollingUp
            } else {
                // When not scrolling, maintain the last known direction
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
                lastScrollDirection
            }
        }
    }.value
}