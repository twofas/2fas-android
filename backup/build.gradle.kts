@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.backup"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":core"))
    implementation(project(":core:common"))
    implementation(project(":core:di"))
    implementation(project(":design"))
    implementation(project(":extensions"))
    implementation(project(":permissions"))
    implementation(project(":prefs"))
    implementation(project(":persistence"))
    implementation(project(":push"))
    implementation(project(":resources"))
    implementation(project(":navigation"))
    implementation(project(":services:domain"))
    implementation(project(":serialization"))
    implementation(project(":time:domain"))
    implementation(project(":data:services"))
    implementation(project(":data:session"))

    implementation(libs.bundles.fastAdapter)
    implementation(libs.bundles.rxJava)
    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.materialDialogs)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewModel)
    implementation(libs.kotlinCoroutines)
}
