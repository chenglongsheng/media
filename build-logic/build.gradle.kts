plugins {
    `kotlin-dsl`
}

repositories {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencies {
    compileOnly(libs.agp)
    compileOnly(libs.kgp)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = libs.plugins.media.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}