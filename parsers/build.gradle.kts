@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.parsers"
}

dependencies {
    implementation(project(":di"))
    implementation(project(":prefs"))
}