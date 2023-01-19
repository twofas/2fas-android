package com.twofasapp.designsystem.composable

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.twofasapp.designsystem.TwsTheme

@Composable
fun TwsPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TwsTheme.typo.labelLarge,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = TwsTheme.color.buttonPrimary,
            contentColor = TwsTheme.color.onButtonPrimary,
        ),
        shape = TwsTheme.shape.roundedButton,
        enabled = enabled,
        modifier = modifier.height(TwsTheme.dimen.buttonHeight),
    ) {
        Text(
            text = text,
            style = style,
        )
    }
}


@Composable
fun TwsTextButton(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TwsTheme.typo.labelLarge,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        Text(
            text = text,
            style = style,
            color = TwsTheme.color.primary
        )
    }
}