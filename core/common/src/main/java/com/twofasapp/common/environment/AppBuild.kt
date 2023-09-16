package com.twofasapp.common.environment

interface AppBuild {
    val id: String
    val debuggable: Boolean
    val versionName: String
    val versionCode: Int
    val buildVariant: BuildVariant
    val deviceName: String
}