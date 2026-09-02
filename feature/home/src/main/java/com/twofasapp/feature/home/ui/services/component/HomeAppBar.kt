package com.twofasapp.feature.home.ui.services.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Badge
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.anim.AnimatedFadeVisibility
import com.twofasapp.core.design.foundation.button.IconButton
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.icon.Icon
import com.twofasapp.core.design.foundation.image.Image
import com.twofasapp.core.design.foundation.layout.ActionsRow
import com.twofasapp.core.design.foundation.menu.DropdownMenu
import com.twofasapp.core.design.foundation.menu.DropdownMenuItem
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.textfield.TextField
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.locale.MdtLocale

@Composable
internal fun HomeAppBar(
    query: String,
    isInEditMode: Boolean,
    isSearchFocused: Boolean,
    hasUnreadNotifications: Boolean,
    developerModeEnabled: Boolean = false,
    selectedCount: Int,
    onEditModeChange: () -> Unit = {},
    onSortClick: () -> Unit = {},
    onAddGroupClick: () -> Unit = {},
    onDeleteSelectedConfirmed: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onDeveloperClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    focusRequester: FocusRequester,
) {
    var showDeleteConfirmationPrompt by remember { mutableStateOf(false) }

    AnimatedContent(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .padding(bottom = 4.dp),
        targetState = isInEditMode,
        transitionSpec = {
            (
                slideInVertically(
                    animationSpec = tween(250),
                    initialOffsetY = { _ -> -24 },
                ) + fadeIn(
                    animationSpec = tween(200),
                )
                )
                .togetherWith(
                    slideOutVertically(
                        animationSpec = tween(250),
                        targetOffsetY = { _ -> 24 },
                    ) + fadeOut(
                        animationSpec = tween(200),
                    ),
                )
                .using(
                    SizeTransform(clip = false),
                )
        },
        label = "topBarAnimation",
    ) { editMode ->
        if (editMode) {
            TopAppBar(
                showBackButton = false,
                content = {
                    Row(
                        modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            icon = MdtIcons.Close,
                            iconTint = MdtTheme.color.onSurface,
                            onClick = onEditModeChange,
                            modifier = Modifier.offset(x = (-10).dp),
                        )

                        Text(
                            text = MdtLocale.strings.trashSelectedItems.format(selectedCount),
                            style = MdtTheme.typo.lg.medium,
                        )
                    }
                },
                actions = {
                    ActionsRow(
                        spacing = 8.dp,
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        IconButton(
                            icon = MdtIcons.Delete,
                            iconTint = MdtTheme.color.primary,
                            enabled = selectedCount > 0,
                            onClick = { showDeleteConfirmationPrompt = true },
                        )

                        IconButton(
                            icon = MdtIcons.Sort,
                            iconTint = MdtTheme.color.primary,
                            onClick = onSortClick,
                        )

                        IconButton(
                            icon = MdtIcons.AddGroup,
                            iconTint = MdtTheme.color.primary,
                            onClick = onAddGroupClick,
                        )
                    }
                },
            )
        } else {
            TopAppBar(
                showBackButton = false,
                content = {
                    SearchBar(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .height(56.dp),
                        query = query,
                        focused = isSearchFocused,
                        hasUnreadNotifications = hasUnreadNotifications,
                        developerModeEnabled = developerModeEnabled,
                        onToggleEditMode = onEditModeChange,
                        onNotificationsClick = onNotificationsClick,
                        onDeveloperClick = onDeveloperClick,
                        onSearchQueryChange = onSearchQueryChange,
                        onSearchFocusChange = onSearchFocusChange,
                        focusRequester = focusRequester,
                    )
                },
            )
        }
    }

    if (showDeleteConfirmationPrompt) {
        ConfirmDialog(
            onDismissRequest = { showDeleteConfirmationPrompt = false },
            title = MdtLocale.strings.commonDelete,
            body = MdtLocale.strings.servicesDeleteSelected.format(selectedCount),
            icon = MdtIcons.Delete,
            onPositive = { onDeleteSelectedConfirmed() },
        )
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier,
    query: String,
    focused: Boolean,
    hasUnreadNotifications: Boolean,
    developerModeEnabled: Boolean,
    onToggleEditMode: () -> Unit,
    onNotificationsClick: () -> Unit,
    onDeveloperClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    focusRequester: FocusRequester,
) {
    var showDropdown by remember { mutableStateOf(false) }
    val searchActive = focused || query.isNotEmpty()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(MdtTheme.color.surfaceContainer)
            .padding(start = 16.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedFadeVisibility(visible = searchActive.not()) {
                Image(
                    painter = painterResource(id = com.twofasapp.core.design.R.drawable.logo_2fas),
                    modifier = Modifier
                        .size(24.dp)
                        .then(
                            if (developerModeEnabled) {
                                Modifier
                                    .testTag("homeDeveloperButton")
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) { onDeveloperClick() }
                            } else {
                                Modifier
                            },
                        ),
                )
            }

            AnimatedFadeVisibility(visible = searchActive) {
                Icon(
                    painter = MdtIcons.Search,
                    tint = MdtTheme.color.onSurface,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        TextField(
            value = query,
            onValueChange = { onSearchQueryChange(it) },
            textStyle = MdtTheme.typo.base.normal,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            placeholder = {
                Text(
                    text = MdtLocale.strings.commonSearch,
                    style = MdtTheme.typo.base.normal,
                    color = MdtTheme.color.onSurfaceVariant.copy(alpha = 0.7f),
                )
            },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Press) {
                                onSearchFocusChange(true)
                            }
                        }
                    }
                },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchFocusChange(false) }),
            colors = OutlinedTextFieldDefaults.colors().copy(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
        )

        AnimatedContent(
            targetState = searchActive,
            transitionSpec = {
                fadeIn(animationSpec = tween(200))
                    .togetherWith(fadeOut(animationSpec = tween(200)))
                    .using(SizeTransform(clip = false))
            },
            label = "searchBarActions",
        ) { searchActive ->
            if (searchActive) {
                IconButton(
                    modifier = Modifier.testTag("searchClearButton").padding(end = 4.dp),
                    icon = MdtIcons.Close,
                    iconTint = MdtTheme.color.onSurface,
                    iconSize = 20.dp,
                    onClick = {
                        if (query.isNotEmpty()) {
                            onSearchQueryChange("")
                        } else {
                            onSearchFocusChange(false)
                        }
                    },
                )
            } else {
                ActionsRow {
                    Box {
                        IconButton(
                            icon = MdtIcons.Notification,
                            iconTint = MdtTheme.color.iconTint,
                            onClick = onNotificationsClick,
                        )

                        if (hasUnreadNotifications) {
                            Badge(
                                containerColor = MdtTheme.color.primary,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(start = 18.dp, bottom = 18.dp),
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false },
                        anchor = { IconButton(icon = MdtIcons.More, onClick = { showDropdown = true }, iconTint = MdtTheme.color.iconTint) },
                    ) {
                        DropdownMenuItem(
                            text = MdtLocale.strings.servicesManageList,
                            icon = MdtIcons.Edit,
                            onClick = {
                                onToggleEditMode()
                                showDropdown = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val focusRequester = remember { FocusRequester() }

    PreviewTheme {
        HomeAppBar(
            query = "",
            isInEditMode = false,
            isSearchFocused = false,
            hasUnreadNotifications = true,
            selectedCount = 0,
            onSearchQueryChange = {},
            onSearchFocusChange = {},
            focusRequester = focusRequester,
        )
    }
}

@Preview
@Composable
private fun PreviewEditMode() {
    val focusRequester = remember { FocusRequester() }

    PreviewTheme {
        HomeAppBar(
            query = "",
            isInEditMode = true,
            isSearchFocused = false,
            hasUnreadNotifications = false,
            developerModeEnabled = true,
            selectedCount = 3,
            onSearchQueryChange = {},
            onSearchFocusChange = {},
            focusRequester = focusRequester,
        )
    }
}