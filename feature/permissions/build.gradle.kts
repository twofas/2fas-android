/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.twofasLint)
}

android {
    namespace = "com.twofasapp.feature.permissions"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:locale"))
    implementation(project(":core:android"))
    implementation(project(":core:common"))

    implementation(platform(libs.composeBom))
    implementation(libs.bundles.compose)
}