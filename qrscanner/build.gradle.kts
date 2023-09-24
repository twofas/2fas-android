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
    implementation(project(":core:common"))
    implementation(project(":design"))
    implementation(project(":resources"))
    
    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.barcodeScanner)
    implementation(libs.bundles.compose)
    implementation(libs.timber)
}