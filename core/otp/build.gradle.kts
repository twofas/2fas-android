@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.otp"
}

dependencies {
    implementation(libs.apacheCommonsCodec)
}