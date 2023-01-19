package com.twofasapp.about.ui

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ShareCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.android.play.core.review.ReviewManagerFactory
import com.twofasapp.resources.R
import com.twofasapp.design.compose.HeaderEntry
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.design.theme.AppThemeLegacy
import com.twofasapp.design.theme.divider
import com.twofasapp.design.theme.isNight
import com.twofasapp.design.theme.textSecondary
import com.twofasapp.extensions.openBrowserApp
import org.koin.androidx.viewmodel.ext.android.viewModel

class AboutActivity : ComponentActivity() {

    private val viewModel: AboutViewModel by viewModel()

    companion object Screens {
        private const val About = "about"
        private const val Licenses = "licenses"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppThemeLegacy {
                Surface {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = About) {
                        composable(About) { About(navController) }
                        composable(Licenses) { Licenses() }
                    }
                }
            }
        }
    }

    @Composable
    private fun About(navController: NavHostController) {
        val uiState = viewModel.uiState.collectAsState()
        val context = LocalContext.current

        Scaffold(
            topBar = { Toolbar(title = stringResource(id = R.string.settings__about)) { onBackPressed() } }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {

                LazyColumn(modifier = Modifier.weight(1f)) {
                    item { HeaderEntry(text = stringResource(id = R.string.settings__general)) }
                    item {
                        SimpleEntry(
                            title = stringResource(id = R.string.settings__write_a_review),
                            icon = painterResource(id = R.drawable.ic_about_write_review),
                            click = {
                                val manager = ReviewManagerFactory.create(this@AboutActivity)
                                val request = manager.requestReviewFlow()
                                request.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val flow = manager.launchReviewFlow(
                                            this@AboutActivity,
                                            task.result
                                        )
                                        flow.addOnCompleteListener {
                                            viewModel.reviewDone()
                                        }
                                    } else {

                                    }
                                }
                            }
                        )
                    }

                    item {
                        SimpleEntry(
                            title = stringResource(id = R.string.settings__privacy_policy),
                            icon = painterResource(id = R.drawable.ic_about_privacy_policy),
                            click = { openBrowserApp(url = "https://2fas.com/privacy-policy/") }
                        )
                    }

                    item {
                        SimpleEntry(
                            title = stringResource(id = R.string.settings__terms_of_service),
                            icon = painterResource(id = R.drawable.ic_about_terms),
                            click = { openBrowserApp(url = "https://2fas.com/terms-of-service/") }
                        )
                    }

                    item {
                        SimpleEntry(
                            title = stringResource(id = R.string.about_licenses),
                            icon = painterResource(id = R.drawable.ic_about_licenses),
                            click = { navController.navigate(Licenses) }
                        )
                    }

                    item { Divider(color = MaterialTheme.colors.divider) }

                    item { HeaderEntry(text = stringResource(id = R.string.settings__share_app)) }
                    item {
                        SimpleEntry(
                            title = stringResource(id = R.string.settings__tell_a_friend),
                            icon = painterResource(id = R.drawable.ic_about_share),
                            click = {
                                ShareCompat.IntentBuilder(context)
                                    .setType("text/plain")
                                    .setChooserTitle("Share 2FAS")
                                    .setText(getString(R.string.settings__recommendation))
                                    .startChooser()
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "App version ${uiState.value.versionName}",
                        color = MaterialTheme.colors.textSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .align(Bottom)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.logo_2fas),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun Licenses() {
        val isNight = isNight()

        Scaffold(
            topBar = { Toolbar(title = stringResource(id = R.string.about_licenses)) { onBackPressed() } }
        ) { padding ->
            AndroidView(factory = {
                WebView(this).apply {
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && isNight) {
                        WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_ON);
                    }

                    webViewClient = WebViewClient()
                    try {
                        loadUrl("file:///android_asset/open_source_licenses.html")
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "There is no WebView installed. Can not display licenses.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }
            })
        }
    }
}
