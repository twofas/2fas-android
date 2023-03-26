@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.twofasapp.feature.externalimport"
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
    implementation(project(":core:di"))
    implementation(project(":resources"))
    implementation(project(":extensions"))
    implementation(project(":core"))
    implementation(project(":design"))
    
    implementation(project(":prefs"))
    implementation(project(":services:domain"))
    implementation(project(":services"))
    implementation(project(":qrscanner"))
    implementation(project(":permissions"))
    implementation(project(":backup:domain"))
    implementation(project(":serialization"))

    implementation(project(":core:common"))
    implementation(project(":core:locale"))
    implementation(project(":core:designsystem"))
    implementation(project(":data:services"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewModel)
    implementation(libs.bundles.accompanist)
    implementation(libs.kotlinCoroutines)
    implementation(libs.webkit)
    implementation(libs.protobuf)
    implementation(libs.apacheCommonsCodec)

    implementation("com.google.api-client:google-api-client-android:2.2.0") {
        exclude("org.apache.httpcomponents", "guava-jdk5")
        exclude("com.google.http-client","google-http-client")
    }

    implementation(platform("com.google.firebase:firebase-bom:31.1.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.1")
}