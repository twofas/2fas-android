package com.twofasapp.feature.externalimport.ui.authenticatorpro

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twofasapp.core.encoding.encodeBase64ToString
import com.twofasapp.feature.externalimport.ui.common.ImportDescription
import com.twofasapp.feature.externalimport.ui.common.ImportFilePickerButton
import com.twofasapp.feature.externalimport.ui.common.ImportFileScaffold
import com.twofasapp.resources.R

@Composable
internal fun AuthenticatorProRoute(
    onFilePicked: (String) -> Unit,
) {

    ImportFileScaffold(
        title = stringResource(id = R.string.externalimport__authenticatorpro),
        image = painterResource(id = R.drawable.ic_import_authenticatorpro),
        description = { ImportDescription(text = stringResource(id = R.string.externalimport__authenticatorpro_msg)) }
    ) {
        ImportFilePickerButton(
            text = stringResource(id = R.string.externalimport__choose_txt_cta),
            fileType = "text/*",
            onFilePicked = { onFilePicked(it.toString().encodeBase64ToString()) }
        )
    }
}