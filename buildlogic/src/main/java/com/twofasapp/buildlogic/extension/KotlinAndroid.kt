package com.twofasapp.buildlogic.extension

import com.android.build.api.dsl.CommonExtension
import com.twofasapp.buildlogic.version.AppConfig
import com.twofasapp.buildlogic.version.AppConfig.compileSdk
import com.twofasapp.buildlogic.version.AppConfig.minSdk
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

internal fun Project.applyKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = AppConfig.compileSdk

        defaultConfig {
            minSdk = AppConfig.minSdk

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            testInstrumentationRunnerArguments += mapOf(
                "clearPackageData" to "true",
                "disableAnalytics" to "true"
            )
        }

        compileOptions {
            isCoreLibraryDesugaringEnabled = true

            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        buildFeatures {
            viewBinding = true
            buildConfig = true
        }

        kotlinOptions {
            jvmTarget = "17"

            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlin.Experimental",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.lifecycle.compose.ExperimentalLifecycleComposeApi",
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
            )
        }

        packaging {
            resources.excludes.add("META-INF/DEPENDENCIES")
            resources.excludes.add("META-INF/LICENSE")
            resources.excludes.add("META-INF/LICENSE.txt")
            resources.excludes.add("META-INF/license.txt")
            resources.excludes.add("META-INF/NOTICE")
            resources.excludes.add("META-INF/NOTICE.txt")
            resources.excludes.add("META-INF/notice.txt")
            resources.excludes.add("META-INF/ASL2.0")
            resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }

        testOptions {
            unitTests {
                isIncludeAndroidResources = true
            }
        }
    }

    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("desugar").get())
    }
}

internal fun CommonExtension<*, *, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}
