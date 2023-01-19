@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.extensions"
}

dependencies {
    implementation(project(":resources"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.rxJava)
    implementation(libs.bundles.rxBinding)
    implementation(libs.bundles.materialDialogs)

}