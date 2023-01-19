@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    id(libs.plugins.twofasCompose.get().pluginId)
}

android {
    namespace = "com.twofasapp.locale"
}

dependencies {
    implementation(libs.bundles.compose)
}