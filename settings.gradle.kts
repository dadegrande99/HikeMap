val localPropertiesFile = file("local.properties")
val localProperties = java.util.Properties()

localPropertiesFile.inputStream().use {
    localProperties.load(it)
}

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://jcenter.bintray.com")
            maven { url = uri("https://jitpack.io") }
        }
        jcenter()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven {
            url = uri("https://jcenter.bintray.com")
            maven { url = uri("https://jitpack.io") }
        }
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }

            credentials {
                // Do not change the username below.
                // This should always be `mapbox` (not your username).
                username = "mapbox"
                // Use the secret token you stored in gradle.properties as the password
                // implement a value for the token form local.properties
                password = localProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN")
            }

        }



    }
}

rootProject.name = "HikeMap"
include(":app")
 