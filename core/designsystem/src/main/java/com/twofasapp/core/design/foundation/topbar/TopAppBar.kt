package com.twofasapp.core.design.foundation.topbar

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme

@Composable
fun TopAppBar(
    titleText: String? = null,
    title: @Composable () -> Unit = {},
    containerColor: Color = MdtTheme.color.background,
    contentColor: Color = MdtTheme.color.onSurfacePrimary,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    showBackButton: Boolean = true,
    onBackClick: (() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit) = {
        if (showBackButton) {
            BackButton(onBackClick)
        }
    },
) {
    TopAppBar(
        title = titleText?.let {
            {
                Text(
                    text = it,
                    color = contentColor,
                    style = MdtTheme.typo.title,
                )
            }
        } ?: title,
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
        modifier = modifier,
    )
}

@Composable
fun BackButton(
    onBackClick: (() -> Unit)? = null,
    tint: Color = MdtTheme.color.onSurfacePrimary,
) {
    val onBackDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    IconButton(onClick = { onBackClick?.invoke() ?: onBackDispatcher?.onBackPressed() }) {
        Icon(
            painter = MdtIcons.ArrowBack,
            contentDescription = null,
            tint = tint,
        )
    }
}