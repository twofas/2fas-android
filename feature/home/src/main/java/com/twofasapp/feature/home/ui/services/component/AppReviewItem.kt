package com.twofasapp.feature.home.ui.services.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.TextButton
import com.twofasapp.locale.TwLocale

@Composable
internal fun AppReviewItem(
    modifier: Modifier = Modifier,
    onRateClick: () -> Unit = {},
    onDismissClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MdtTheme.color.surface)
            .padding(all = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MdtTheme.color.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = MdtIcons.StarShine,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MdtTheme.color.primary,
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = TwLocale.strings.homeAppReviewTitle,
                    style = MdtTheme.typo.body1.copy(fontWeight = FontWeight.Medium),
                    color = MdtTheme.color.onSurfacePrimary,
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = TwLocale.strings.homeAppReviewMsg,
                    style = MdtTheme.typo.body3,
                    color = MdtTheme.color.onSurfaceSecondary,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                text = TwLocale.strings.homeAppReviewDismiss,
                onClick = onDismissClick,
            )

            Button(
                text = TwLocale.strings.homeAppReviewRate,
                height = 36.dp,
                leadingIcon = MdtIcons.Star,
                leadingIconTint = Color.White,
                onClick = onRateClick,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppReviewItem(Modifier.fillMaxWidth())
}