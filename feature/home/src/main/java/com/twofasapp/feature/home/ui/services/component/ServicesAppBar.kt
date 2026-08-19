package com.twofasapp.feature.home.ui.services.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.IconButton
import com.twofasapp.core.design.foundation.icon.Icon
import com.twofasapp.core.design.foundation.image.Image
import com.twofasapp.core.design.foundation.menu.DropdownMenu
import com.twofasapp.core.design.foundation.menu.DropdownMenuItem
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.locale.R
import com.twofasapp.locale.TwLocale

@Composable
internal fun ServicesAppBar(
    query: String,
    isInEditMode: Boolean,
    isSearchFocused: Boolean,
    hasUnreadNotifications: Boolean,
    onEditModeChange: () -> Unit = {},
    onSortClick: () -> Unit = {},
    onAddGroupClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    focusRequester: FocusRequester,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    AnimatedVisibility(
        visible = isInEditMode,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        TopAppBar(
            title = stringResource(id = R.string.tokens__manage_list),
            onBackClick = onEditModeChange,
            scrollBehavior = scrollBehavior,
            actions = {
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
            },
        )
    }

    AnimatedVisibility(
        visible = isInEditMode.not(),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        TopAppBar(
            content = {
                SearchBar(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(56.dp),
                    query = query,
                    focused = isSearchFocused,
                    onToggleEditMode = onEditModeChange,
                    onSearchQueryChange = onSearchQueryChange,
                    onSearchFocusChange = onSearchFocusChange,
                    focusRequester = focusRequester,
                )
            },
            showBackButton = false,
            scrollBehavior = scrollBehavior,
            actions = {
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
            },
        )
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier,
    query: String,
    focused: Boolean,
    onToggleEditMode: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    focusRequester: FocusRequester,
) {
    var showDropdown by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MdtTheme.color.surface)
            .padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(visible = focused.not() && query.isEmpty()) {
            Image(painter = painterResource(id = com.twofasapp.core.design.R.drawable.logo_2fas), modifier = Modifier.size(24.dp))
        }

        AnimatedVisibility(visible = focused || query.isNotEmpty()) {
            Icon(painter = MdtIcons.Search, modifier = Modifier.size(24.dp))
        }

        TextField(
            value = query,
            onValueChange = { onSearchQueryChange(it) },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            placeholder = {
                Text(
                    text = TwLocale.strings.commonSearch,
                    style = MdtTheme.typo.regular.base.copy(fontSize = 18.sp),
                )
            },
            textStyle = MdtTheme.typo.regular.base.copy(fontSize = 18.sp),
            colors = TextFieldDefaults.colors(
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = MdtTheme.color.onSurface,
                focusedPlaceholderColor = MdtTheme.color.onSurfaceVariant,
                unfocusedPlaceholderColor = MdtTheme.color.onSurfaceVariant,
                unfocusedContainerColor = MdtTheme.color.surface,
                focusedContainerColor = MdtTheme.color.surface,
                disabledContainerColor = MdtTheme.color.surface,
                errorContainerColor = MdtTheme.color.surface,
            ),
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
        )

        AnimatedVisibility(visible = focused || query.isNotEmpty()) {
            IconButton(
                icon = MdtIcons.Close,
                onClick = {
                    if (query.isNotEmpty()) {
                        onSearchQueryChange("")
                    } else {
                        onSearchFocusChange(false)
                    }
                },
            )
        }

        AnimatedVisibility(visible = focused.not() && query.isEmpty()) {
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                anchor = { IconButton(icon = MdtIcons.More, onClick = { showDropdown = true }) },
            ) {
                DropdownMenuItem(
                    text = TwLocale.strings.servicesManageList,
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