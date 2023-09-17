@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.widgets"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":core:di"))
    implementation(project(":resources"))
    implementation(project(":prefs"))
    implementation(project(":design"))
    

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.fastAdapter)
    implementation(libs.bundles.rxJava)
    implementation(libs.webkit)
}