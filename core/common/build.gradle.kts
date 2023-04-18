@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.common"
}

dependencies {
    implementation(project(":core:di"))
    implementation(libs.kotlinCoroutines)
}