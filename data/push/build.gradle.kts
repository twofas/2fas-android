@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.data.push"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.kotlinCoroutines)
    implementation(libs.timber)
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseCrashlytics)
    implementation(libs.firebaseMessaging)
    implementation(libs.playServicesCorutines)
}