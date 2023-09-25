@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.push"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":prefs"))

    implementation(project(":core:common"))

    implementation(libs.kotlinCoroutines)
    implementation(libs.timber)

    implementation(platform("com.google.firebase:firebase-bom:31.1.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.1")
}