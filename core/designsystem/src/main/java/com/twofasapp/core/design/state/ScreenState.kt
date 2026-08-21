/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

data class ScreenState(
    val content: Content = Content.Success,
    val loading: Boolean = true,
    val pullRefreshing: Boolean = false,
) {
    sealed interface Content {
        data object Success : Content
        data class Empty(val msg: String? = null) : Content
        data class Error(val msg: String? = null) : Content
    }

    companion object {
        val Loading = ScreenState(loading = true)
        val Empty = ScreenState(loading = false, pullRefreshing = false, content = Content.Empty())
        val Success = ScreenState(loading = false, pullRefreshing = false, content = ScreenState.Content.Success)
    }
}

fun MutableStateFlow<ScreenState>.loading(pullRefresh: Boolean = false) {
    update { it.copy(loading = pullRefresh.not(), pullRefreshing = pullRefresh) }
}

fun MutableStateFlow<ScreenState>.notLoading() {
    update { it.copy(loading = false, pullRefreshing = false) }
}

fun MutableStateFlow<ScreenState>.success() {
    update { it.copy(loading = false, pullRefreshing = false, content = ScreenState.Content.Success) }
}

fun MutableStateFlow<ScreenState>.empty(msg: String? = null) {
    update { it.copy(loading = false, pullRefreshing = false, content = ScreenState.Content.Empty(msg)) }
}

fun MutableStateFlow<ScreenState>.error(msg: String? = null) {
    update { it.copy(loading = false, pullRefreshing = false, content = ScreenState.Content.Error(msg)) }
}