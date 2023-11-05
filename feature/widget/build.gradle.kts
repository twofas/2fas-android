@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.feature.widget"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":prefs"))
    implementation(project(":core:common"))
    implementation(project(":core:android"))
    implementation(project(":core:locale"))
    implementation(project(":core:designsystem"))
    implementation(project(":data:services"))
    implementation(project(":data:session"))

    implementation(libs.bundles.viewModel)
    implementation(libs.bundles.glance)
    implementation(libs.bundles.compose)
    implementation(libs.workManager)
    implementation(libs.timber)
}