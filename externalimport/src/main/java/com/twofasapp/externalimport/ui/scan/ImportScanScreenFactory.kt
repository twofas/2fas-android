package com.twofasapp.externalimport.ui.scan

import androidx.compose.runtime.Composable

class ImportScanScreenFactory {

    @Composable
    fun create(startWithGallery: Boolean) {
        ImportScanScreen(startWithGallery)
    }
}