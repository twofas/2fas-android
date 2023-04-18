@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.design"
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":prefs"))
    implementation(project(":extensions"))
    implementation(project(":resources"))
    implementation(project(":parsers"))

    implementation(project(":core:designsystem"))
    implementation(project(":data:session"))

    implementation(libs.bundles.fastAdapter)
    implementation(libs.bundles.materialDialogs)
    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.accompanist)
}