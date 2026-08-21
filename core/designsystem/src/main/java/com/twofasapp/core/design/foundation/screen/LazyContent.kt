/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.screen

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.state.ScreenState

@Composable
fun LazyContent(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    screenState: ScreenState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    alignment: Alignment = Alignment.Center,
    emptyIcon: Painter? = null,
    loadingState: @Composable (ColumnScope.() -> Unit)? = null,
    emptyState: @Composable (ColumnScope.() -> Unit)? = null,
    errorState: @Composable (ColumnScope.() -> Unit)? = null,
    itemsAlwaysVisible: (LazyListScope.() -> Unit)? = null,
    itemsWhenSuccess: (LazyListScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            contentPadding = contentPadding,
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            flingBehavior = flingBehavior,
            userScrollEnabled = userScrollEnabled,
        ) {
            itemsAlwaysVisible?.invoke(this)

            if (screenState.content is ScreenState.Content.Success && screenState.loading.not()) {
                itemsWhenSuccess?.invoke(this)
            }
        }

        if (screenState.loading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = alignment,
            ) {
                loadingState?.invoke(this@Column) ?: ScreenLoading()
            }
        } else {
            when (screenState.content) {
                is ScreenState.Content.Empty -> {
                    emptyState?.invoke(this) ?: ScreenEmpty(
                        text = screenState.content.msg.orEmpty(),
                        icon = emptyIcon ?: MdtIcons.Info,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                    )
                }

                is ScreenState.Content.Error -> {
                    errorState?.invoke(this) ?: ScreenError(
                        text = screenState.content.msg.orEmpty(),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                    )
                }

                is ScreenState.Content.Success -> Unit
            }
        }
    }
}