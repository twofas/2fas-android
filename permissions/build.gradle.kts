@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.permissions"
}

dependencies {
    implementation(project(":di"))
    implementation(project(":resources"))
    implementation(project(":extensions"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.rxJava)
    implementation(libs.bundles.materialDialogs)
}