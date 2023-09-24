@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.security"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":core:common"))
    implementation(project(":core:locale"))
    implementation(project(":core:android"))
    implementation(project(":core:designsystem"))
    implementation(project(":data:session"))
    implementation(project(":resources"))
    implementation(project(":design"))
    implementation(project(":prefs"))

    implementation(project(":security:domain"))
    implementation(project(":core:common"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
    implementation(libs.biometric)
}