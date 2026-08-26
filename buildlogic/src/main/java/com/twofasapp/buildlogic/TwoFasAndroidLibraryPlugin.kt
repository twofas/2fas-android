package com.twofasapp.buildlogic

import com.android.build.api.dsl.LibraryExtension
import com.twofasapp.buildlogic.extension.applyKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class TwoFasAndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            extensions.configure<LibraryExtension> {
                applyKotlinAndroid(this)
                defaultConfig.multiDexEnabled = true
            }
        }
    }
}
