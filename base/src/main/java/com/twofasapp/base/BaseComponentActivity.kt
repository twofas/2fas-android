package com.twofasapp.base

import android.content.ComponentCallbacks
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.twofasapp.base.lifecycle.AuthAware
import com.twofasapp.base.lifecycle.AuthLifecycle
import com.twofasapp.resources.R
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier

abstract class BaseComponentActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.BUILD_TYPE.equals("release", true)) {
            window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

        if (resources.getBoolean(R.bool.isPortraitOnly)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        logLifecycle("onCreate", LifecycleOperation.IN)
        attachAuthLifecycleObserver()
    }

    override fun onResume() {
        super.onResume()
        logLifecycle("onResume", LifecycleOperation.IN)
    }

    override fun onPause() {
        super.onPause()
        logLifecycle("onPause", LifecycleOperation.OUT)
    }

    override fun onDestroy() {
        super.onDestroy()
        logLifecycle("onDestroy", LifecycleOperation.OUT)
    }

    private fun attachAuthLifecycleObserver() {
        lifecycle.addObserver(
            AuthLifecycle(
                authTracker = get(),
                navigator = get { parametersOf(this) },
                authAware = this as? AuthAware
            )
        )
    }

    inline fun <reified T : Any> ComponentCallbacks.injectThis(
        qualifier: Qualifier? = null,
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    ) = lazy(mode) { get<T>(qualifier, { parametersOf(this) }) }
}