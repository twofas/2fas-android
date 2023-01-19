@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.common"
}

dependencies {
    implementation(project(":di"))
    implementation(libs.kotlinCoroutines)
}