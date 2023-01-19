package com.twofasapp

import com.twofasapp.environment.AppConfig
import com.twofasapp.environment.BuildVariant

class AppConfigImpl : AppConfig {

    override val id: String = BuildConfig.APPLICATION_ID

    override val isDebug: Boolean = BuildConfig.DEBUG

    override val versionName: String = BuildConfig.VERSION_NAME

    override val versionCode: Int = BuildConfig.VERSION_CODE

    override val buildVariant: BuildVariant = when (BuildConfig.BUILD_TYPE) {
        "debug" -> BuildVariant.Debug
        "releaseLocal" -> BuildVariant.ReleaseLocal
        "release" -> BuildVariant.Release
        else -> throw RuntimeException("Unknown build variant!")
    }

    override val deviceName: String = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL
}