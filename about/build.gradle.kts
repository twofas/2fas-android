@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.about"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":di"))
    implementation(project(":resources"))
    implementation(project(":extensions"))
    implementation(project(":design"))
    implementation(project(":environment"))
    implementation(project(":featuretoggle"))
    implementation(project(":prefs"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.playReview)
    implementation(libs.webkit)

}