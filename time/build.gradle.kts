@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.time"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":time:domain"))
    implementation(project(":prefs"))
    implementation(project(":core:network"))
    implementation(project(":truetime"))

    implementation(libs.timber)
    implementation(libs.workManager)
}