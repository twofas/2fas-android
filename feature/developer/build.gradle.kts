@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.twofasLint)
}

android {
    namespace = "com.twofasapp.feature.developer"
}

dependencies {
    implementation(project(":core:android"))
    implementation(project(":core:common"))
    implementation(project(":core:locale"))
    implementation(project(":core:designsystem"))
    implementation(project(":data:services"))
    implementation(project(":data:session"))

    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewModel)
}
