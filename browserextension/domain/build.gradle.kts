@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinParcelize)
}

android {
    namespace = "com.twofasapp.browserextension.domain"
}


dependencies {
    implementation(project(":base"))
    implementation(project(":time:domain"))
    implementation(libs.bundles.rxJava)
    implementation(libs.kotlinCoroutines)
}
