@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.data.session"
}

dependencies {
    implementation(project(":di"))
    implementation(project(":core:storage"))

    implementation(libs.kotlinCoroutines)
    implementation(libs.kotlinSerialization)
}