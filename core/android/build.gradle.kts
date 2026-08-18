@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
//    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.twofasLint)
}

android {
    namespace = "com.twofasapp.android"
}

dependencies {
    implementation(libs.bundles.compose)
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.viewModel)
    implementation(libs.kotlinCoroutines)
}