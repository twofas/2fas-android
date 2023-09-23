@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinParcelize)
}

android {
    namespace = "com.twofasapp.feature.browserext"
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":core:common"))
    implementation(project(":core:android"))
    implementation(project(":core:locale"))
    implementation(project(":core:designsystem"))

    implementation(project(":data:browserext"))
    implementation(project(":data:session"))
    implementation(project(":data:services"))

    implementation(project(":feature:qrscan"))

    implementation(project(":security"))
    implementation(project(":security:domain"))
    implementation(project(":push"))

    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewModel)
    implementation(libs.bundles.accompanist)
    implementation(libs.kotlinSerialization)
    implementation(libs.lottie)
    implementation(libs.workManager)
}