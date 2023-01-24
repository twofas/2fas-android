package com.twofasapp.feature.home.ui.services

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.twofasapp.designsystem.common.TwIconButton
import com.twofasapp.designsystem.common.TwTopAppBar

@Composable
internal fun ServicesAppBar(
    isInEditMode: Boolean,
    onEditModeChange: () -> Unit,
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
            scrollBehavior = scrollBehavior
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
    var expanded by remember { mutableStateOf(false) }

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
                    textColor = Color.Gray,
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )


            Box {
                TwIconButton(painter = TwIcons.More, onClick = { expanded = true })
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .widthIn(min = 160.dp)
                        .background(TwTheme.color.surface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Manage list", modifier = Modifier.padding(start = 16.dp)) },
                        onClick = {
                            onToggleEditMode()
                            expanded = false
                        },
                        modifier = Modifier.background(TwTheme.color.surface)
                    )

                }
            }
        }
    }
}