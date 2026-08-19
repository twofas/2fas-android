package com.twofasapp.feature.home.ui.services.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.foundation.text.richText
import com.twofasapp.feature.home.R
import com.twofasapp.locale.TwLocale

@Composable
internal fun PassBanner(
    modifier: Modifier = Modifier,
    onGoToStoreClick: () -> Unit = {},
    onDismissClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .height(IntrinsicSize.Min),
    ) {
        Image(
            painter = painterResource(R.drawable.pass_banner_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.pass_banner_icon),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = TwLocale.strings.passBannerTitle,
                color = MdtTheme.color.onSurface,
                style = MdtTheme.typo.regular.base.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = richText(TwLocale.strings.passBannerMsg),
                color = MdtTheme.color.onSurface,
                style = MdtTheme.typo.regular.sm,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    text = TwLocale.strings.passBannerNegativeCta,
                    style = ButtonStyle.Outlined,
                    height = 36.dp,
                    onClick = onDismissClick,
                    // Adapted from OutlinedButton(textColor/borderColor = onSurface): the new Button's
                    // Outlined border color is fixed (MdtTheme.color.outline); only content color is set here.
                    contentColor = MdtTheme.color.onSurface,
                )
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    text = "Go to Store",
                    height = 36.dp,
                    onClick = onGoToStoreClick,
                    containerColor = Color(0xFF064AD7),
                    contentColor = Color.White,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PassBanner(Modifier.fillMaxWidth())
}