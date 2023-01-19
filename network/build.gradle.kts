@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.network"
}

dependencies {
    implementation(project(":di"))
    implementation(project(":serialization"))
    implementation(project(":prefs"))
    implementation(project(":environment"))

    implementation(libs.bundles.ktor)
    implementation(libs.timber)
}