plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasLint)
}

android {
    namespace = "com.twofasapp.data.logs"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.bundles.room)
    implementation(libs.kotlinCoroutines)
}
