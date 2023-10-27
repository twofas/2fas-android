package com.twofasapp.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.twofasapp.buildlogic.extension.applyBuildTypes
import com.twofasapp.buildlogic.extension.applyKotlinAndroid
import com.twofasapp.buildlogic.extension.applySigningConfigs
import com.twofasapp.buildlogic.version.AppConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class TwoFasAndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                applyKotlinAndroid(this)
                applySigningConfigs(this)
                applyBuildTypes(this)

                defaultConfig {
                    multiDexEnabled = true
                    targetSdk = AppConfig.targetSdk

                    vectorDrawables.useSupportLibrary = true
                }

                bundle {
                    abi { enableSplit = true }
                    density { enableSplit = true }
                }

                lint {
                    checkReleaseBuilds = false
                    abortOnError = false
                }
            }
        }
    }
}
