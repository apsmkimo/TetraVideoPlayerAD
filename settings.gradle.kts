pluginManagement {
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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// rootProject.name = "QuadVideoPlayer"
rootProject.name = "TetraVideoPlayer"
include(":app")
// Official androidx.media3:media3-decoder-ffmpeg is not on Maven; local 1.11.0 module.
include(":decoder-ffmpeg")
