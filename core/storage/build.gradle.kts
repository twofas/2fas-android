@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.storage"
}

dependencies {
    implementation(project(":di"))
    implementation(libs.kotlinCoroutines)
    implementation(libs.kotlinSerialization)
    implementation(libs.securityCrypto)
    implementation(libs.timber)
}