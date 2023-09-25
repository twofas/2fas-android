@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinParcelize)
}

android {
    namespace = "com.twofasapp.prefs"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:storage"))

    implementation(libs.bundles.appCompat)
    implementation(libs.timber)
    implementation(libs.securityCrypto)
    implementation(libs.secureStorage)
    implementation(libs.kotlinCoroutines)
    implementation(libs.kotlinSerialization)
}