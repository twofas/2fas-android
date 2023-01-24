@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.di"
}

dependencies {
    api(libs.bundles.koin)
    api(libs.javaxInject)
}