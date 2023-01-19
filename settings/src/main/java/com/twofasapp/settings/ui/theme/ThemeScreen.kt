package com.twofasapp.settings.ui.theme

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.twofasapp.design.compose.SwitchEntry
import com.twofasapp.design.compose.SwitchEntryType
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.navigation.SettingsDirections
import com.twofasapp.navigation.SettingsRouter
import com.twofasapp.prefs.model.AppTheme
import com.twofasapp.resources.R
import org.koin.androidx.compose.get

@Composable
internal fun ThemeScreen(
    viewModel: ThemeViewModel = get(),
    router: SettingsRouter = get()
) {
    val uiState = viewModel.uiState.collectAsState().value

    if (uiState.recreateActivity) {
        (LocalContext.current as? Activity)?.recreate()
    }

    Scaffold(
        topBar = {
            Toolbar(title = stringResource(id = R.string.settings__option_theme)) {
                router.navigate(SettingsDirections.GoBack)
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {

            item {
                SwitchEntry(
                    title = stringResource(id = R.string.settings__theme_option_auto_system),
                    isChecked = uiState.theme == AppTheme.AUTO,
                    type = SwitchEntryType.Radio,
                    iconVisible = false,
                    switch = { viewModel.changeTheme(AppTheme.AUTO) }
                )
            }

            item {
                SwitchEntry(
                    title = stringResource(id = R.string.settings__theme_option_light),
                    isChecked = uiState.theme == AppTheme.LIGHT,
                    type = SwitchEntryType.Radio,
                    iconVisible = false,
                    switch = { viewModel.changeTheme(AppTheme.LIGHT) }
                )
            }

            item {
                SwitchEntry(
                    title = stringResource(id = R.string.settings__theme_option_dark),
                    isChecked = uiState.theme == AppTheme.DARK,
                    type = SwitchEntryType.Radio,
                    iconVisible = false,
                    switch = { viewModel.changeTheme(AppTheme.DARK) }
                )
            }
        }
    }
}