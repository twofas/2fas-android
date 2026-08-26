@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.twofasLint)
}

android {
    namespace = "com.twofasapp.storage"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.kotlinCoroutines)
    implementation(libs.kotlinSerialization)
    implementation(libs.securityCrypto)
    implementation(libs.timber)
}