plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasLint)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.common"
}

dependencies {
    implementation(libs.kotlinCoroutines)
    implementation(libs.kotlinSerialization)
    implementation(libs.dataStore)
    api(libs.bundles.koin)
    api(libs.javaxInject)
}