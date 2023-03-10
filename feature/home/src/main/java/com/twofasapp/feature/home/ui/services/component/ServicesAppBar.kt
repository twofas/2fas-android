package com.twofasapp.feature.home.ui.services.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwDropdownMenu
import com.twofasapp.designsystem.common.TwDropdownMenuItem
import com.twofasapp.designsystem.common.TwIconButton
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.locale.TwLocale

@Composable
internal fun ServicesAppBar(
    isInEditMode: Boolean,
    onEditModeChange: () -> Unit = {},
    onSortClick: () -> Unit = {},
    onAddGroupClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
) {
    AnimatedVisibility(
        visible = isInEditMode,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        TwTopAppBar(
            titleText = "Manage list",
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
                        .padding(end = 16.dp)
                        .height(56.dp),
                    onToggleEditMode = onEditModeChange,
                )
            },
            showBackButton = false,
            scrollBehavior = scrollBehavior,
        )
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier,
    onToggleEditMode: () -> Unit,
) {
    var dropdownVisible by remember { mutableStateOf(false) }

    val animVisibleState = remember { MutableTransitionState(false) }
    animVisibleState.targetState = true
    val transition = updateTransition(animVisibleState)

    AnimatedVisibility(visible = true) {

        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(TwTheme.color.surface)
                .padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_2fas), contentDescription = null, modifier = Modifier.size(24.dp)
            )

            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.weight(1f),
                placeholder = { Text(text = "Search") },
                colors = TextFieldDefaults.textFieldColors(
                    focusedTextColor = Color.Gray,
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            TwDropdownMenu(
                expanded = dropdownVisible,
                onDismissRequest = { dropdownVisible = false },
                anchor = { TwIconButton(painter = TwIcons.More, onClick = { dropdownVisible = true }) }
            ) {
                TwDropdownMenuItem(
                    text = TwLocale.strings.servicesManageList,
                    icon = TwIcons.Edit,
                    onClick = {
                        onToggleEditMode()
                        dropdownVisible = false
                    },
                )
            }
        }
    }
}