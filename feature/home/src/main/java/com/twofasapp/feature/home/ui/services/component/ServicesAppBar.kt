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
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwDropdownMenu
import com.twofasapp.designsystem.common.TwDropdownMenuItem
import com.twofasapp.designsystem.common.TwIcon
import com.twofasapp.designsystem.common.TwIconButton
import com.twofasapp.designsystem.common.TwImage
import com.twofasapp.designsystem.common.TwTopAppBar
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
        TwTopAppBar(
            titleText = stringResource(id = R.string.tokens__manage_list),
            onBackClick = onEditModeChange,
            scrollBehavior = scrollBehavior,
            actions = {
                TwIconButton(
                    painter = TwIcons.Sort,
                    tint = TwTheme.color.primary,
                    onClick = onSortClick,
                )

                TwIconButton(
                    painter = TwIcons.AddGroup,
                    tint = TwTheme.color.primary,
                    onClick = onAddGroupClick,
                )
            }
        )
    }

    AnimatedVisibility(
        visible = isInEditMode.not(),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        TwTopAppBar(
            title = {
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
                    TwIconButton(
                        painter = TwIcons.Notification,
                        tint = TwTheme.color.iconTint,
                        onClick = onNotificationsClick,
                    )

                    if (hasUnreadNotifications) {
                        Badge(
                            containerColor = TwTheme.color.primary,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(start = 18.dp, bottom = 18.dp)
                        )
                    }
                }
            }
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
            .background(TwTheme.color.surface)
            .padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AnimatedVisibility(visible = focused.not() && query.isEmpty()) {
            TwImage(painter = painterResource(id = com.twofasapp.designsystem.R.drawable.logo_2fas), modifier = Modifier.size(24.dp))
        }

        AnimatedVisibility(visible = focused || query.isNotEmpty()) {
            TwIcon(painter = TwIcons.Search, modifier = Modifier.size(24.dp))
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
                    style = TwTheme.typo.body1.copy(fontSize = 18.sp)
                )
            },
            textStyle = TwTheme.typo.body1.copy(fontSize = 18.sp),
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = TwTheme.color.onSurfacePrimary,
                focusedPlaceholderColor = TwTheme.color.onSurfaceSecondary,
                unfocusedPlaceholderColor = TwTheme.color.onSurfaceSecondary,
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
            keyboardActions = KeyboardActions(onSearch = { onSearchFocusChange(false) })
        )

        AnimatedVisibility(visible = focused || query.isNotEmpty()) {
            TwIconButton(
                painter = TwIcons.Close,
                onClick = {
                    if (query.isNotEmpty()) {
                        onSearchQueryChange("")
                    } else {
                        onSearchFocusChange(false)
                    }
                }
            )
        }

        AnimatedVisibility(visible = focused.not() && query.isEmpty()) {
            TwDropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                anchor = { TwIconButton(painter = TwIcons.More, onClick = { showDropdown = true }) }
            ) {
                TwDropdownMenuItem(
                    text = TwLocale.strings.servicesManageList,
                    icon = TwIcons.Edit,
                    onClick = {
                        onToggleEditMode()
                        showDropdown = false
                    },
                )
            }
        }
    }
}