@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinParcelize)
}

android {
    namespace = "com.twofasapp.browserextension"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":core"))
    implementation(project(":di"))
    implementation(project(":design"))
    implementation(project(":extensions"))
    implementation(project(":permissions"))
    implementation(project(":prefs"))
    implementation(project(":persistence"))
    implementation(project(":network"))
    implementation(project(":push"))
    implementation(project(":qrscanner"))
    implementation(project(":environment"))
    implementation(project(":navigation"))
    implementation(project(":services:domain"))
    implementation(project(":time:domain"))
    implementation(project(":serialization"))
    implementation(project(":resources"))
    implementation(project(":security:domain"))
    implementation(project(":browserextension:domain"))

    implementation(libs.bundles.fastAdapter)
    implementation(libs.bundles.rxJava)
    implementation(libs.bundles.appCompat)
    implementation(libs.lottie)
    implementation(libs.timber)
    implementation(libs.kotlinCoroutines)
    implementation(libs.workManager)
    implementation(libs.workManagerRx)
    implementation(libs.bundles.viewModel)
    implementation(libs.bundles.compose)
}
