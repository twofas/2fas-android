@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinParcelize)
}

android {
    namespace = "com.twofasapp.prefs"
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":serialization"))
    
    implementation(project(":time:domain"))
    implementation(project(":extensions"))

    implementation(project(":core:storage"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.rxJava)
    implementation(libs.timber)
    implementation(libs.securityCrypto)
    implementation(libs.secureStorage)
    implementation(libs.kotlinCoroutines)
}