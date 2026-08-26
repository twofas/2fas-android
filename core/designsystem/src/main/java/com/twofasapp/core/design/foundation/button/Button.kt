package com.twofasapp.core.design.foundation.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.textButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme

@Composable
fun Button(
    text: String,
    onClick: () -> Unit = {},
    height: Dp = MdtTheme.dimen.buttonHeight,
    modifier: Modifier = Modifier,
    style: TextStyle = MdtTheme.typo.body2,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MdtTheme.color.primary,
        contentColor = Color.White,
    ),
    leadingIcon: Painter? = null,
    leadingIconSize: Dp = 18.dp,
    leadingIconTint: Color = Color.Unspecified,
    leadingIconSpacer: Dp = 6.dp,
) {
    Button(
        onClick = onClick,
        colors = colors,
        enabled = enabled,
        modifier = modifier.height(height),
    ) {
        if (leadingIcon != null) {
            if (leadingIconTint == Color.Unspecified) {
                Image(
                    painter = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(leadingIconSize),
                )
            } else {
                Icon(
                    painter = leadingIcon,
                    contentDescription = null,
                    tint = leadingIconTint,
                    modifier = Modifier.size(leadingIconSize),
                )
            }
            Spacer(modifier = Modifier.width(leadingIconSpacer))
        }

        Text(
            text = text,
            style = style,
        )
    }
}

@Composable
fun OutlinedButton(
    text: String,
    onClick: () -> Unit,
    height: Dp = MdtTheme.dimen.buttonHeight,
    modifier: Modifier = Modifier,
    style: TextStyle = MdtTheme.typo.body2,
    enabled: Boolean = true,
    textColor: Color = MdtTheme.color.primary,
    borderColor: Color = MdtTheme.color.primary,
) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(
            width = 1.dp,
            color = borderColor,
        ),
        enabled = enabled,
        modifier = modifier.height(height),
    ) {
        Text(
            text = text,
            style = style,
            color = textColor,
        )
    }
}

@Composable
fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    style: TextStyle = MdtTheme.typo.body2,
    enabled: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign? = null,
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = textButtonColors(
            contentColor = MdtTheme.color.primary,
            disabledContentColor = MdtTheme.color.onSurfaceSecondary,
        ),
    ) {
        Text(
            text = text,
            style = style,
            maxLines = maxLines,
            textAlign = textAlign,
        )
    }
}

@Composable
fun IconButton(
    painter: Painter? = null,
    contentDescription: String? = null,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    tint: Color? = null,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    content: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    ) {
        content?.invoke() ?: painter?.let {
            Icon(
                painter = it,
                contentDescription = contentDescription,
                modifier = iconModifier,
                tint = tint ?: MdtTheme.color.iconTint,
            )
        }
    }
}