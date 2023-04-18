package com.twofasapp.designsystem.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.R
import com.twofasapp.designsystem.screen.CommonContentDescription
import com.twofasapp.designsystem.screen.CommonContentTitle
import com.twofasapp.locale.TwLocale

@Composable
fun TwEmptyScreen(
    title: String? = null,
    body: String? = null,
    image: Painter? = null,
    additionalContent: @Composable() (ColumnScope.() -> Unit) = {},
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (image != null) {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.height(120.dp)
            )
        }

        if (title != null) {
            CommonContentTitle(text = title)
        }

        if (body != null) {
            CommonContentDescription(text = body)
        }

        additionalContent()

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    TwEmptyScreen(
        body = TwLocale.strings.placeholderLong,
        image = painterResource(id = R.drawable.ic_placeholder),
        modifier = Modifier.fillMaxSize(),
    )
}