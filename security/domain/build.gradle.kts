@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.security.domain"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":prefs"))
    implementation(project(":persistence"))
    implementation(project(":push"))
    
    implementation(project(":resources"))
    implementation(project(":parsers"))
    implementation(project(":truetime"))
    implementation(project(":truetime-rx"))
    implementation(project(":time:domain"))

    implementation(libs.bundles.fastAdapter)
    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
}
