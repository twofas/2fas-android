@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinKapt)
}

android {
    namespace = "com.twofasapp.persistence"

    kapt {
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
}

dependencies {
    implementation(project(":di"))
    implementation(project(":prefs"))
    implementation(project(":environment"))
    implementation(project(":parsers"))

    implementation(libs.bundles.room)
    kapt(libs.roomCompiler)
}