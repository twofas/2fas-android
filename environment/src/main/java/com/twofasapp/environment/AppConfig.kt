package com.twofasapp.environment

interface AppConfig {
    val id: String
    val isDebug: Boolean
    val versionName: String
    val versionCode: Int
    val buildVariant: BuildVariant
    val deviceName: String
}
