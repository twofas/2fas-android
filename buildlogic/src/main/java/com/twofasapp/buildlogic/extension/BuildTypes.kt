package com.twofasapp.buildlogic.extension

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project

internal fun Project.applyBuildTypes(
    applicationExtension: ApplicationExtension,
) {
    applicationExtension.apply {
        buildTypes {
            getByName("debug") {
                isMinifyEnabled = false
                isDebuggable = true
                signingConfig = signingConfigs.getByName("debug")
                applicationIdSuffix = ".debug"
            }
            create("releaseLocal") {
                isMinifyEnabled = true
                isDebuggable = false
                signingConfig = signingConfigs.getByName("releaseLocal")
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                matchingFallbacks += "release"
            }
            getByName("release") {
                isMinifyEnabled = true
                isDebuggable = false
                signingConfig = signingConfigs.getByName("release")
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            }
        }
    }
}