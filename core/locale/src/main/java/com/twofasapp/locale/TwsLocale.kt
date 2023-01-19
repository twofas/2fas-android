package com.twofasapp.locale

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

object TwsLocale {
    val strings: Strings
        @Composable
        get() = Strings(LocalContext.current)

    val links: Links = Links()
}