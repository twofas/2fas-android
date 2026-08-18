@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasLint)
}

android {
    namespace = "com.twofasapp.otp"
}

dependencies {
    implementation(libs.apacheCommonsCodec)
}