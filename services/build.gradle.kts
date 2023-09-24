@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
}

android {
    namespace = "com.twofasapp.services"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":core:di"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:locale"))
    implementation(project(":design"))
    implementation(project(":prefs"))
    implementation(project(":resources"))
    implementation(project(":persistence"))
    implementation(project(":push"))
    implementation(project(":qrscanner"))

    implementation(project(":time:domain"))
    
    implementation(project(":parsers"))
    implementation(project(":data:services"))
    implementation(project(":data:session"))
    implementation(project(":core:android"))

    implementation(libs.bundles.fastAdapter)
    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.materialDialogs)
    implementation(libs.timber)
    implementation(libs.lottie)
    implementation(libs.bundles.compose)
}
