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
    implementation(project(":core"))
    implementation(project(":design"))
    implementation(project(":extensions"))
    implementation(project(":permissions"))
    implementation(project(":prefs"))
    implementation(project(":resources"))
    implementation(project(":persistence"))
    implementation(project(":push"))
    implementation(project(":qrscanner"))
    
    implementation(project(":services:domain"))
    implementation(project(":widgets:domain"))
    implementation(project(":time:domain"))
    implementation(project(":navigation"))
    implementation(project(":parsers"))
    implementation(project(":backup:domain"))
    implementation(project(":spanner"))
    implementation(project(":data:services"))
    implementation(project(":data:session"))

    implementation(libs.bundles.fastAdapter)
    implementation(libs.bundles.rxJava)
    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.materialDialogs)
    implementation(libs.timber)
    implementation(libs.lottie)
    implementation(libs.bundles.compose)
}
