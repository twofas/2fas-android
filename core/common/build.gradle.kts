@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.common"
}

dependencies {
    implementation(libs.kotlinCoroutines)
    api(libs.bundles.koin)
    api(libs.javaxInject)
}