package com.twofasapp.buildlogic

import com.android.build.gradle.LibraryExtension
import com.twofasapp.buildlogic.extension.applyKotlinAndroid
import com.twofasapp.buildlogic.version.AppConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class TwoFasAndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                applyKotlinAndroid(this)
                defaultConfig.targetSdk = AppConfig.targetSdk
                defaultConfig.multiDexEnabled = true
            }
        }
    }
}
