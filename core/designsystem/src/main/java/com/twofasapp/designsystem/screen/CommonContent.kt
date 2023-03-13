package com.twofasapp.designsystem.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.locale.TwLocale

@Composable
fun CommonContent(
    image: Painter? = null,
    titleText: String? = null,
    descriptionText: String? = null,
    ctaPrimaryText: String? = null,
    ctaPrimaryClick: () -> Unit = {},
    title: @Composable (() -> Unit)? = null,
    description: @Composable (() -> Unit)? = null,
    cta: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (image != null) {
                Image(painter = image, contentDescription = null, Modifier.height(TwTheme.dimen.commonContentImageHeight))
                Spacer(Modifier.height(24.dp))
            }

            if (titleText != null) {
                CommonContentTitle(text = titleText)
            }

            title?.invoke()

            Spacer(Modifier.height(16.dp))

            if (descriptionText != null) {
                CommonContentDescription(text = descriptionText)
            }

            description?.invoke()

            Spacer(Modifier.height(64.dp))
        }

        if (ctaPrimaryText != null) {
            TwButton(
                text = ctaPrimaryText,
                onClick = ctaPrimaryClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        cta?.invoke()
    }
}

@Composable
fun CommonContentTitle(text: String) {
    Text(
        text = text,
        style = TwTheme.typo.title,
        color = TwTheme.color.onSurfacePrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    )
}

@Composable
fun CommonContentDescription(text: String) {
    Text(
        text = text,
        style = TwTheme.typo.body1,
        color = TwTheme.color.onSurfacePrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    )
}


@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    CommonContent(
        image = TwIcons.Placeholder,
        titleText = TwLocale.strings.placeholder,
        descriptionText = TwLocale.strings.placeholderLong,
        ctaPrimaryText = TwLocale.strings.placeholder,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

