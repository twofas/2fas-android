package com.twofasapp.designsystem.common


import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme

@Composable
fun TwTopAppBar(
    titleText: String? = null,
    title: @Composable () -> Unit = {},
    containerColor: Color = TwTheme.color.background,
    contentColor: Color = TwTheme.color.onSurfacePrimary,
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
                    style = TwTheme.typo.title
                )
            }
        } ?: title,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
fun TwCenterTopAppBar(
    titleText: String? = null,
    title: @Composable () -> Unit = {},
    containerColor: Color = TwTheme.color.background,
    contentColor: Color = TwTheme.color.onSurfacePrimary,
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
    CenterAlignedTopAppBar(
        title = titleText?.let {
            {
                Text(
                    text = it,
                    color = contentColor,
                    style = TwTheme.typo.title
                )
            }
        } ?: title,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
fun TwMediumTopAppBar(
    titleText: String? = null,
    title: @Composable () -> Unit = {},
    containerColor: Color = TwTheme.color.surface,
    contentColor: Color = TwTheme.color.onSurfacePrimary,
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
    MediumTopAppBar(
        title = titleText?.let {
            {
                Text(
                    text = it,
                    color = contentColor,
                    style = TwTheme.typo.title
                )
            }
        } ?: title,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
fun TwLargeTopAppBar(
    titleText: String? = null,
    title: @Composable () -> Unit = {},
    containerColor: Color = TwTheme.color.surface,
    contentColor: Color = TwTheme.color.onSurfacePrimary,
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
    LargeTopAppBar(
        title = titleText?.let {
            {
                Text(
                    text = it,
                    color = contentColor,
                    style = TwTheme.typo.title
                )
            }
        } ?: title,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

@Composable
fun BackButton(
    onBackClick: (() -> Unit)? = null,
    tint: Color = TwTheme.color.onSurfacePrimary,
) {
    val onBackDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    IconButton(onClick = { onBackClick?.invoke() ?: onBackDispatcher?.onBackPressed() }) {
        Icon(
            painter = TwIcons.ArrowBack,
            contentDescription = null,
            tint = tint,
        )
    }
}