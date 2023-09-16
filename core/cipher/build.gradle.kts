@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.cipher"
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":core:common"))

    implementation(libs.kotlinCoroutines)
    implementation(libs.kotlinSerialization)
}