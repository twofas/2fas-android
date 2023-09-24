@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.parsers"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":prefs"))
    implementation(libs.timber)
    implementation(libs.kotlinSerialization)
}