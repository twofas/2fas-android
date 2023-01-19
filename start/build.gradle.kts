@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.kotlinParcelize)
}

android {
    namespace = "com.twofasapp.start"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":di"))
    implementation(project(":core"))
    implementation(project(":resources"))
    implementation(project(":extensions"))
    implementation(project(":environment"))
    implementation(project(":design"))
    implementation(project(":prefs"))
    implementation(project(":navigation"))
    implementation(project(":featuretoggle"))
    implementation(project(":security:domain"))
    implementation(project(":services:domain"))
    implementation(project(":start:domain"))
    implementation(project(":time:domain"))
    implementation(project(":parsers"))
    implementation(project(":core:storage"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
    implementation(libs.kotlinCoroutines)
    implementation(libs.timber)
    implementation(libs.workManager)
}