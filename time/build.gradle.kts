@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.time"
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":time:domain"))
    implementation(project(":prefs"))
    implementation(project(":truetime"))
    implementation(project(":truetime-rx"))

    implementation(libs.timber)
    implementation(libs.workManager)
    implementation(libs.coroutinesToRx)
}