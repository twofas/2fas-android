@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.feature.home"
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":data:notifications"))
    implementation(project(":data:services"))
    implementation(project(":data:session"))
    implementation(project(":core:common"))
    implementation(project(":core:android"))
    implementation(project(":core:locale"))
    implementation(project(":core:designsystem"))
    implementation(project(":backup:domain"))
    implementation(project(":feature:qrscan"))
    implementation(project(":parsers"))
    implementation(project(":resources"))

    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewModel)
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.commonmark)
    implementation(libs.timber)
    implementation(libs.kotlinSerialization)
}