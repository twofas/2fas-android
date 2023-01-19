package com.twofasapp.settings.ui.main

import android.app.Activity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.SubtitleGravity
import com.twofasapp.design.compose.SwitchEntry
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.navigation.SettingsDirections
import com.twofasapp.navigation.SettingsRouter
import com.twofasapp.prefs.model.AppTheme
import com.twofasapp.resources.R
import org.koin.androidx.compose.get

@Composable
internal fun SettingsMainScreen(
    viewModel: SettingsMainViewModel = get(),
    router: SettingsRouter = get(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val activity = (LocalContext.current as? Activity)

    Scaffold(
        topBar = { Toolbar(title = stringResource(id = R.string.settings__settings)) { activity?.onBackPressed() } }
    ) { padding ->
        LazyColumn {
            item {
                SimpleEntry(
                    title = stringResource(id = R.string.settings__option_theme),
                    subtitle = when (uiState.theme) {
                        AppTheme.AUTO -> stringResource(R.string.settings__theme_option_auto)
                        AppTheme.LIGHT -> stringResource(R.string.settings__theme_option_light)
                        AppTheme.DARK -> stringResource(R.string.settings__theme_option_dark)
                    },
                    subtitleGravity = SubtitleGravity.END,
                    icon = painterResource(id = R.drawable.ic_option_theme),
                    click = { router.navigate(SettingsDirections.Theme) }
                )
            }

            item {
                SwitchEntry(
                    title = stringResource(id = R.string.settings__show_next_token),
                    icon = painterResource(id = R.drawable.ic_next_token),
                    isChecked = uiState.showNextToken,
                    switch = { isChecked -> viewModel.changeShowNextToken(isChecked) }
                )
            }

            item {
                SimpleEntry(
                    title = stringResource(id = R.string.browser__browser_extension),
                    icon = painterResource(id = R.drawable.ic_option_browser_extension),
                    click = { router.navigate(SettingsDirections.BrowserExtension) }
                )
            }

        }
    }
}
