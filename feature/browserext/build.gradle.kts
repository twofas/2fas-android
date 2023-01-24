@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.feature.browserext"
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":core:common"))
    implementation(project(":core:locale"))
    implementation(project(":core:designsystem"))

    implementation(project(":browserextension")) // TO BE REMOVED
    implementation(project(":navigation")) // TO BE REMOVED
0
    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewModel)
    implementation(libs.bundles.accompanist)
}