/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.topbar

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme

@Composable
fun TopAppBar(
    title: String? = null,
    content: @Composable () -> Unit = {},
    containerColor: Color = MdtTheme.color.background,
    contentColor: Color = MdtTheme.color.onSurface,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    showBackButton: Boolean = true,
    height: Dp? = null,
    onBackClick: (() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit) = {
        if (showBackButton) {
            BackButton(onBackClick)
        }
    },
) {
    TopAppBar(
        title = title?.let { { TopAppBarTitle(text = it, color = contentColor) } } ?: content,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor,
        ),
        scrollBehavior = scrollBehavior,
        expandedHeight = height ?: TopAppBarDefaults.TopAppBarExpandedHeight,
        modifier = modifier,
    )
}

@Composable
fun BackButton(onBackClick: (() -> Unit)? = null) {
    val onBackDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    IconButton(
        modifier = Modifier.testTag("backButton"),
        onClick = { onBackClick?.invoke() ?: onBackDispatcher?.onBackPressed() },
    ) {
        Icon(
            painter = MdtIcons.ArrowBack,
            contentDescription = null,
        )
    }
}