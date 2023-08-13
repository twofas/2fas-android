package com.twofasapp.buildlogic.version

object AppConfig {
    const val minSdk = 23
    const val targetSdk = 34
    const val compileSdk = 34

    private const val verMajor = 4
    private const val verMinor = 6
    private const val verPatch = 3
    private const val verInternal = 0

    const val versionCode = verMajor * 1000000 + verMinor * 10000 + verPatch * 100 + verInternal
    const val versionName = "${verMajor}.${verMinor}.${verPatch}"
    const val apkName = "TwoFas-${verMajor}.${verMinor}.${verPatch}-${verInternal}"
}