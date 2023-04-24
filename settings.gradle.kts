pluginManagement {
    includeBuild("buildlogic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        jcenter()
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "2fas-android"
include(":truetime")
include(":truetime-rx")
include(":spanner")
include(":app")
include(":browserextension")
include(":base")
include(":prefs")
include(":serialization")
include(":time")
include(":parsers")
include(":resources")
include(":extensions")
include(":design")
include(":permissions")
include(":push")
include(":persistence")
include(":qrscanner")
include(":widgets")
include(":services")
include(":services:domain")
include(":widgets:domain")
include(":backup")
include(":core")
include(":featuretoggle")
include(":developer")
include(":backup:domain")
include(":security")
include(":security:domain")
include(":start")
include(":start:domain")
include(":time:domain")
include(":feature:externalimport")
include(":data:session")
include(":core:storage")
include(":core:common")
include(":core:di")
include(":core:designsystem")
include(":feature:startup")
include(":core:locale")
include(":feature:home")
include(":data:notifications")
include(":core:network")
include(":data:services")
include(":feature:trash")
include(":feature:about")
include(":data:browserext")
include(":feature:browserext")
include(":core:otp")
include(":feature:appsettings")