package com.twofasapp.externalimport.ui.result

import androidx.compose.runtime.Composable

class ImportResultScreenFactory {

    @Composable
    fun create(type: String, content: String) {
        return ImportResultScreen(type, content)
    }
}