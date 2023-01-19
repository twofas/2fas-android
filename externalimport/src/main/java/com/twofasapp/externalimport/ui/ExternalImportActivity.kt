package com.twofasapp.externalimport.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import com.twofasapp.base.BaseComponentActivity
import com.twofasapp.design.theme.AppThemeLegacy
import com.twofasapp.navigation.ExternalImportRouter
import com.twofasapp.navigation.base.RouterNavHost
import org.koin.androidx.compose.get

class ExternalImportActivity : BaseComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppThemeLegacy {
                Surface {
                    RouterNavHost(router = get<ExternalImportRouter>())
                }
            }
        }
    }
}
