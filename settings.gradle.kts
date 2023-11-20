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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
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
                    /*
                    password =

                    "sk.eyJ1IjoiYW5kcmV3aGlrZW1hcCIsImEiOiJjbG93NTM4cmowemxlMnFxZTF0NDVzNmR2In0.EFydrVrCOvngq-4A6dEcqg"

                     */
            }

        }
    }
}

rootProject.name = "HikeMap"
include(":app")
 