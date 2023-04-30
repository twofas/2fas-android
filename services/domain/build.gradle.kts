@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.services.domain"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":core:di"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":design"))
    implementation(project(":extensions"))
    implementation(project(":permissions"))
    implementation(project(":prefs"))
    implementation(project(":persistence"))
    implementation(project(":push"))
    
    implementation(project(":resources"))
    implementation(project(":parsers"))
    implementation(project(":time:domain"))
    implementation(project(":truetime"))
    implementation(project(":truetime-rx"))

    implementation("commons-codec:commons-codec:1.15")
    implementation(libs.bundles.fastAdapter)
    implementation(libs.bundles.rxJava)
    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
    implementation(libs.apacheCommonsCodec)
}
