@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.notifications"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":core"))
    implementation(project(":di"))
    implementation(project(":design"))
    implementation(project(":resources"))
    implementation(project(":extensions"))
    implementation(project(":persistence"))
    implementation(project(":prefs"))
    implementation(project(":network"))
    implementation(project(":environment"))
    implementation(project(":time:domain"))

    implementation(libs.bundles.appCompat)
    implementation(libs.kotlinCoroutines)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewModel)
    implementation(libs.timber)
}
