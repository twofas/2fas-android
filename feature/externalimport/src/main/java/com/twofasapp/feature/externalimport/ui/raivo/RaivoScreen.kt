package com.twofasapp.feature.externalimport.ui.raivo

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twofasapp.core.encoding.encodeBase64ToString
import com.twofasapp.feature.externalimport.ui.common.ImportDescription
import com.twofasapp.feature.externalimport.ui.common.ImportFilePickerButton
import com.twofasapp.feature.externalimport.ui.common.ImportFileScaffold
import com.twofasapp.resources.R

@Composable
internal fun RaivoRoute(
    onFilePicked: (String) -> Unit,
) {

    ImportFileScaffold(
        title = stringResource(id = R.string.externalimport_raivo),
        image = painterResource(id = R.drawable.ic_import_raivo),
        description = { ImportDescription(text = "Use the \"Export OTPs to ZIP archive\" option in Raivo's Settings, save a ZIP file, extract it and import the JSON file using the \"Choose JSON file\" button.") }
    ) {
        ImportFilePickerButton(
            text = "Choose JSON file",
            fileType = "application/json",
            onFilePicked = { onFilePicked(it.toString().encodeBase64ToString()) }
        )
    }
}