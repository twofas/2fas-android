@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.featuretoggle"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":core:di"))
    
    implementation(project(":prefs"))
    implementation(project(":core:common"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.rxJava)

    implementation(libs.kotlinCoroutines)
    implementation(libs.timber)

    implementation(platform("com.google.firebase:firebase-bom:31.1.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.1")
}