package com.twofasapp.designsystem.service.atoms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwIcon

@Composable
internal fun ServiceHotp(
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {

    Box(modifier = Modifier
        .size(56.dp)
        .clip(CircleShape)
        .clickable(enabled) { onClick() }
        .alpha(if (enabled) 1f else 0.3f)
    ) {
        TwIcon(
            painter = TwIcons.IncrementHotp,
            tint = TwTheme.color.primary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ServiceHotp()
}