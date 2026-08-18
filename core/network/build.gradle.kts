@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.twofasLint)
}

android {
    namespace = "com.twofasapp.network"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinCoroutines)
    implementation(libs.kotlinSerialization)
    implementation(libs.timber)
    api(libs.bundles.ktor)
}