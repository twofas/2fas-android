@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.qrscanner"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":core:di"))
    implementation(project(":design"))
    implementation(project(":resources"))
    implementation(project(":extensions"))
    implementation(project(":navigation"))
    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.rxJava)
    implementation(libs.bundles.barcodeScanner)
    implementation(libs.bundles.compose)
    implementation(libs.timber)
}