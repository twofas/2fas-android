package com.twofasapp.feature.about.ui.licenses

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.core.text.parseAsHtml
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.locale.MdtLocale
import org.koin.compose.koinInject

@Composable
internal fun AboutLicensesScreen(
    openSourceLibrariesProvider: OpenSourceLibrariesProvider = koinInject(),
) {
    val libraries by produceLibraries(openSourceLibrariesProvider.aboutLibrariesResId)

    Scaffold(
        topBar = { TopAppBar(title = MdtLocale.strings.aboutLicenses) },
    ) { padding ->
        LibrariesContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            libraries = libraries,
            licenseDialogBody = { library, modifier -> LicenseBody(library = library, modifier = modifier) },
        )
    }
}

@Composable
private fun LicenseBody(
    library: Library,
    modifier: Modifier,
) {
    val license = remember(library) {
        library.htmlReadyLicenseContent
            .takeIf { it.isNotEmpty() }
            ?.let { AnnotatedString(it.parseAsHtml().toString()) }
    }

    if (license != null) {
        Text(
            text = license,
            modifier = modifier,
            color = MdtTheme.color.onSurface,
        )
    }
}