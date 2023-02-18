package com.twofasapp.security.ui.security

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import com.twofasapp.base.BaseComponentActivity
import com.twofasapp.designsystem.MainAppTheme
import com.twofasapp.navigation.SecurityRouter
import com.twofasapp.navigation.base.RouterNavHost
import org.koin.androidx.compose.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class SecurityActivity : BaseComponentActivity() {

    private val viewModel: SecurityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelStoreOwner = compositionLocalOf<ViewModelStoreOwner> { this }

        viewModel.init()

        setContent {
            MainAppTheme {
                RouterNavHost(router = get<SecurityRouter>(), viewModelStoreOwner.current)
            }
        }
    }
}