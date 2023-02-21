package com.twofasapp.externalimport.ui.aegis

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twofasapp.core.encoding.encodeBase64ToString
import com.twofasapp.resources.R
import com.twofasapp.externalimport.ui.common.ImportDescription
import com.twofasapp.externalimport.ui.common.ImportFilePickerButton
import com.twofasapp.externalimport.ui.common.ImportFileScaffold
import com.twofasapp.navigation.ExternalImportDirections
import com.twofasapp.navigation.ExternalImportRouter
import org.koin.androidx.compose.get

@Composable
fun AegisScreen(
    router: ExternalImportRouter = get(),
) {

    ImportFileScaffold(
        title = stringResource(id = R.string.externalimport_aegis),
        image = painterResource(id = R.drawable.ic_import_aegis),
        description = { ImportDescription(text = stringResource(id = R.string.externalimport__aegis_msg)) },
        actions = {
            ImportFilePickerButton(
                text = stringResource(id = R.string.externalimport__choose_json_cta),
                fileType = "application/json",
                onFilePicked = {
                    router.navigate(
                        ExternalImportDirections.ImportResult(
                            type = ExternalImportDirections.ImportResult.Type.Aegis,
                            content = it.toString().encodeBase64ToString()
                        )
                    )
                }
            )
        },
        onBackClick = { router.navigateBack() }
    )
}