@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
}

android {
    namespace = "com.twofasapp.widgets.domain"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":core:di"))
    implementation(project(":design"))
    implementation(project(":extensions"))
    implementation(project(":permissions"))
    implementation(project(":prefs"))
    implementation(project(":persistence"))
    implementation(project(":push"))
    

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.fastAdapter)
    implementation(libs.bundles.rxJava)
}
