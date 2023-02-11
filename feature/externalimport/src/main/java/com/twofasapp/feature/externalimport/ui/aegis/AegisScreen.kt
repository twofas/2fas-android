package com.twofasapp.feature.externalimport.ui.aegis

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twofasapp.core.encoding.encodeBase64ToString
import com.twofasapp.feature.externalimport.ui.common.ImportDescription
import com.twofasapp.feature.externalimport.ui.common.ImportFilePickerButton
import com.twofasapp.feature.externalimport.ui.common.ImportFileScaffold
import com.twofasapp.resources.R

@Composable
internal fun AegisRoute(
    onFilePicked: (String) -> Unit,
) {

    ImportFileScaffold(
        title = stringResource(id = R.string.externalimport_aegis),
        image = painterResource(id = R.drawable.ic_import_aegis),
        description = { ImportDescription(text = stringResource(id = R.string.externalimport__aegis_msg)) }
    ) {
        ImportFilePickerButton(
            text = "Choose JSON file",
            fileType = "application/json",
            onFilePicked = {
                onFilePicked(it.toString().encodeBase64ToString())
            }
        )
    }
}