package com.twofasapp.feature.about.ui.licenses

import com.twofasapp.feature.about.R

interface OpenSourceLibrariesProvider {
    val aboutLibrariesResId: Int
}

internal class OpenSourceLibrariesProviderImpl : OpenSourceLibrariesProvider {
    override val aboutLibrariesResId: Int = R.raw.aboutlibraries
}