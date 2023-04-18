@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    id(libs.plugins.twofasCompose.get().pluginId)
}

android {
    namespace = "com.twofasapp.designsystem"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:locale"))

    implementation(libs.core)
    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.coil)
}