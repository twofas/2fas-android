package com.twofasapp.buildlogic

import com.twofasapp.buildlogic.extension.findVersionString
import com.twofasapp.buildlogic.extension.getBuildExtension
import com.twofasapp.buildlogic.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class TwoFasComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            getBuildExtension()?.apply {
                buildFeatures {
                    compose = true
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = libs.findVersionString("composeCompiler")
                }
            }
        }
    }
}
