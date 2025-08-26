@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidApplication)
    alias(libs.plugins.twofasCompose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.twofasapp"

    defaultConfig {
        applicationId = "com.twofasapp"
        versionName = "5.5.1"
        versionCode = 5000032
    }

    applicationVariants.all {
        outputs.all {
            val output = this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output?.outputFileName = "TwoFas-$versionName-${versionCode - 5000000}.apk"
        }
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:android"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:storage"))
    implementation(project(":core:network"))
    implementation(project(":core:locale"))
    implementation(project(":core:cipher"))

    implementation(project(":data:notifications"))
    implementation(project(":data:session"))
    implementation(project(":data:services"))
    implementation(project(":data:browserext"))
    implementation(project(":data:cloud"))
    implementation(project(":data:push"))

    implementation(project(":feature:startup"))
    implementation(project(":feature:home"))
    implementation(project(":feature:trash"))
    implementation(project(":feature:about"))
    implementation(project(":feature:externalimport"))
    implementation(project(":feature:browserext"))
    implementation(project(":feature:appsettings"))
    implementation(project(":feature:qrscan"))
    implementation(project(":feature:backup"))
    implementation(project(":feature:widget"))
    implementation(project(":feature:security"))

    implementation(project(":base"))
    implementation(project(":prefs"))
    implementation(project(":truetime"))
    implementation(project(":secure-storage"))
    implementation(project(":parsers"))

    implementation(libs.bundles.appCompat)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.barcodeScanner)
    implementation(libs.bundles.room)
    implementation(libs.reLinker)
    ksp(libs.roomCompiler)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewModel)
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.playReview)
    implementation(libs.bundles.playUpdate)
    implementation(libs.bundles.glance)
    implementation(libs.biometric)
    implementation(libs.timber)
    implementation(libs.webkit)
    implementation(libs.securityCrypto)
    implementation(libs.lottie)
    implementation(libs.kotlinCoroutines)
    implementation(libs.workManager)
    implementation(libs.activityX)
    implementation(libs.coreSplash)
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseCrashlytics)
    implementation(libs.firebaseMessaging)
    implementation(libs.playServicesCorutines)
    implementation(libs.playFeatureDelivery)
    implementation(libs.googleApiClientGson)
    implementation(libs.googleApiClientAndroid) {
        exclude("org.apache.httpcomponents", "guava-jdk5")
        exclude("com.google.http-client", "google-http-client")
    }
    implementation(libs.googleAuth) {
        exclude("com.google.http-client", "google-http-client")
        exclude("com.google.http-client", "google-http-client-jackson")
    }
    implementation(libs.googleDrive) {
        exclude("org.apache.httpcomponents", "guava-jdk5")
        exclude("com.google.http-client", "google-http-client")
    }

    // ObjectBox - legacy
    debugImplementation("io.objectbox:objectbox-android-objectbrowser:4.3.0")
    releaseImplementation("io.objectbox:objectbox-android:4.3.0")
    implementation("io.objectbox:objectbox-kotlin:4.3.0")
    kapt("io.objectbox:objectbox-processor:4.3.0")
}
