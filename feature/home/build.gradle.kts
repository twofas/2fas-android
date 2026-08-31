plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.twofasLint)
}

android {
    namespace = "com.twofasapp.feature.home"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:android"))
    implementation(project(":core:locale"))
    implementation(project(":core:designsystem"))

    implementation(project(":data:notifications"))
    implementation(project(":data:services"))
    implementation(project(":data:session"))

    implementation(project(":feature:qrscan"))
    implementation(project(":feature:permissions"))

    implementation(project(":parsers"))
    implementation(project(":prefs"))

    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewModel)
    implementation(libs.bundles.playReview)
    implementation(libs.timber)
    implementation(libs.zxing)
}