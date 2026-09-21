plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.twofasLint)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.android"
}

dependencies {
    implementation(project(":core:common"))

    implementation(platform(libs.composeBom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewModel)
    implementation(libs.kotlinCoroutines)
    implementation(libs.navigation3Runtime)
    implementation(libs.kotlinSerialization)
}