package com.twofasapp.buildlogic.extension

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.Properties

internal fun Project.applySigningConfigs(
    applicationExtension: ApplicationExtension,
) {
    applicationExtension.apply {

        val localConfig = Properties().apply {
            load(FileInputStream(File(rootProject.rootDir, "config/config.properties")))
        }

        signingConfigs {
            getByName("debug") {
                storeFile = file("../config/debug_signing.jks")
                storePassword = localConfig.getProperty("debug.storePassword")
                keyAlias = localConfig.getProperty("debug.keyAlias")
                keyPassword = localConfig.getProperty("debug.keyPassword")
            }
            create("release") {
                storeFile = file("../config/release_upload.jks")
                storePassword = localConfig.getProperty("releaseUpload.storePassword")
                keyAlias = localConfig.getProperty("releaseUpload.keyAlias")
                keyPassword = localConfig.getProperty("releaseUpload.keyPassword")
            }
            create("releaseLocal") {
                storeFile = file("../config/release_signing.jks")
                storePassword = localConfig.getProperty("release.storePassword")
                keyAlias = localConfig.getProperty("release.keyAlias")
                keyPassword = localConfig.getProperty("release.keyPassword")
            }
        }
    }
}