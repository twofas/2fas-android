@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.data.services"
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":core:common"))
    implementation(project(":core:storage"))
    implementation(project(":core:otp"))

    implementation(project(":parsers"))

    implementation(libs.bundles.room)
    implementation(libs.kotlinCoroutines)
    implementation(libs.kotlinSerialization)
    implementation(libs.timber)
}