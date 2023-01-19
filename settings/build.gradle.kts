@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.settings"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":di"))
    implementation(project(":resources"))
    implementation(project(":extensions"))
    implementation(project(":design"))
    implementation(project(":prefs"))
    implementation(project(":navigation"))
    implementation(project(":featuretoggle"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
}