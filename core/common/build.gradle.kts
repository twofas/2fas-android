@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasLint)
}

android {
    namespace = "com.twofasapp.common"
}

dependencies {
    implementation(libs.kotlinCoroutines)
    api(libs.bundles.koin)
    api(libs.javaxInject)
}