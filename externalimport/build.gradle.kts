@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.twofasapp.externalimport"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}


dependencies {
    implementation(project(":base"))
    implementation(project(":di"))
    implementation(project(":resources"))
    implementation(project(":extensions"))
    implementation(project(":core"))
    implementation(project(":design"))
    implementation(project(":environment"))
    implementation(project(":prefs"))
    implementation(project(":navigation"))
    implementation(project(":services:domain"))
    implementation(project(":services"))
    implementation(project(":qrscanner"))
    implementation(project(":permissions"))
    implementation(project(":backup:domain"))
    implementation(project(":serialization"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewModel)
    implementation(libs.kotlinCoroutines)
    implementation(libs.webkit)
    implementation(libs.protobuf)

    implementation("com.google.api-client:google-api-client-android:1.32.2")

    implementation(platform("com.google.firebase:firebase-bom:31.1.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.1")
}