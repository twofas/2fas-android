@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.navigation"
}

dependencies {
    implementation(libs.kotlinCoroutines)
    implementation(libs.bundles.compose)
}