package com.twofasapp.feature.home.ui.services.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.locale.MdtLocale

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun ServicesFab(
    isVisible: Boolean,
    isExtendedVisible: Boolean,
    isNormalVisible: Boolean,
    onClick: () -> Unit,
) {
    if (isVisible) {
        if (isExtendedVisible) {
            ExtendedFloatingActionButton(
                onClick = onClick,
                icon = { Icon(MdtIcons.Add, null) },
                text = { Text(text = MdtLocale.strings.servicesEmptyPairServiceCta) },
                containerColor = MdtTheme.color.primary,
                contentColor = Color.White,
            )
        } else {
            AnimatedVisibility(
                visible = isNormalVisible,
                enter = scaleIn(tween(150)),
                exit = scaleOut(tween(150)),
            ) {
                FloatingActionButton(
                    onClick = onClick,
                    content = { Icon(MdtIcons.Add, null) },
                    containerColor = MdtTheme.color.primary,
                    contentColor = Color.White,
                )
            }
        }
    }
}