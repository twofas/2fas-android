@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.security.domain"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":prefs"))

    implementation(project(":parsers"))
    implementation(project(":core:locale"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
}
