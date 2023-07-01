@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.parsers"
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":prefs"))
    implementation(project(":serialization"))
    implementation(libs.timber)
}