package com.twofasapp.feature.about.ui.licenses

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.locale.TwLocale

@Composable
internal fun LicensesRoute() {
    LicensesScreen()
}

@Composable
internal fun LicensesScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = { TwTopAppBar(titleText = TwLocale.strings.aboutLicenses) }
    ) { padding ->
        AndroidView(factory = {
            WebView(context).apply {
//                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && isNight) {
//                    WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_ON);
//                }

                webViewClient = WebViewClient()
                try {
                    loadUrl("file:///android_asset/open_source_licenses.html")
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "There is no WebView installed. Can not display licenses.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }, modifier = Modifier.padding(padding))
    }
}