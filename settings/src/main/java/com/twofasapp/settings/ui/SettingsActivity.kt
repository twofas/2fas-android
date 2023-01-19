package com.twofasapp.settings.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import com.twofasapp.base.BaseComponentActivity
import com.twofasapp.design.theme.AppThemeLegacy
import com.twofasapp.navigation.SettingsRouter
import com.twofasapp.navigation.base.RouterNavHost
import org.koin.androidx.compose.get

class SettingsActivity : BaseComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppThemeLegacy {
                Surface {
                    RouterNavHost(router = get<SettingsRouter>())
                }
            }
        }
    }
}
