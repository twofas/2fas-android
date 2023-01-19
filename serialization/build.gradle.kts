@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.serialization"
}

dependencies {
    implementation(project(":di"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
}