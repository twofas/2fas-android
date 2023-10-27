package com.twofasapp.environment

import com.twofasapp.BuildConfig
import com.twofasapp.common.environment.AppBuild
import com.twofasapp.common.environment.BuildVariant
import com.twofasapp.common.environment.BuildVariant.Debug
import com.twofasapp.common.environment.BuildVariant.Release
import com.twofasapp.common.environment.BuildVariant.ReleaseLocal

class AppBuildImpl : AppBuild {
    override val id: String = BuildConfig.APPLICATION_ID

    override val debuggable: Boolean = BuildConfig.DEBUG

    override val versionName: String = BuildConfig.VERSION_NAME

    override val versionCode: Int = BuildConfig.VERSION_CODE

    override val buildVariant: BuildVariant = when (BuildConfig.BUILD_TYPE) {
        "debug" -> Debug
        "releaseLocal" -> ReleaseLocal
        "release" -> Release
        else -> throw RuntimeException("Unknown build variant!")
    }

    override val deviceName: String = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL
}