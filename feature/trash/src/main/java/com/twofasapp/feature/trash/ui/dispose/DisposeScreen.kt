package com.twofasapp.feature.trash.ui.dispose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwOutlinedButton
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun DisposeRoute(
    navigateBack: () -> Unit,
    viewModel: DisposeViewModel = koinViewModel()
) {
    DisposeScreen(
        onDeleteClick = {
            viewModel.delete()
            navigateBack()
        },
        onFinish = navigateBack
    )
}

@Composable
private fun DisposeScreen(
    onDeleteClick: () -> Unit,
    onFinish: () -> Unit,
) {

    Scaffold(topBar = { TwTopAppBar(TwLocale.strings.trashTitle) }) { padding ->
        Column(Modifier.padding(padding)) {
            TwButton(text = "Delete forever", onClick = onDeleteClick)
            TwOutlinedButton(text = "Cancel", onClick = onFinish)
        }
    }
}