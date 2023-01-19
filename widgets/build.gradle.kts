@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.widgets"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":di"))
    implementation(project(":resources"))
    implementation(project(":extensions"))
    implementation(project(":prefs"))
    implementation(project(":design"))
    implementation(project(":environment"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.fastAdapter)
    implementation(libs.bundles.rxJava)
    implementation(libs.webkit)
}