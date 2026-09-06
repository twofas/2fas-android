@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.twofasLint)
}

android {
    namespace = "com.twofasapp.feature.browserext"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":core:common"))
    implementation(project(":core:android"))
    implementation(project(":core:locale"))
    implementation(project(":core:designsystem"))

    implementation(project(":data:browserext"))
    implementation(project(":data:session"))
    implementation(project(":data:services"))

    implementation(project(":feature:qrscan"))
    implementation(project(":feature:permissions"))

    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewModel)
    implementation(libs.kotlinSerialization)
    implementation(libs.lottie)
    implementation(libs.workManager)
}