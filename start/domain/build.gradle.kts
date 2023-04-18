@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.start.domain"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":core:di"))
}