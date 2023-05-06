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
    implementation(project(":core:di"))
    implementation(project(":prefs"))
    
    implementation(project(":parsers"))
    implementation(project(":data:notifications"))
    implementation(project(":data:services"))
    implementation(project(":data:browserext"))
    implementation(project(":core:common"))

    implementation(libs.bundles.room)
    implementation(libs.reLinker)
    kapt(libs.roomCompiler)
}