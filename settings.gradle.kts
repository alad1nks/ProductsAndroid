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

rootProject.name = "ProductsAndroid"
include(":app")
include(":core")
include(":core:network")
include(":core:data")
include(":core:designsystem")
include(":feature")
include(":feature:products")
include(":core:model")
include(":feature:product")
include(":core:datastore")
include(":core:database")
include(":core:domain")
