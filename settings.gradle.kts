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
        
        // Xposed
        maven {
            url = uri("https://api.xposed.info/")
            content { includeGroup("de.robv.android.xposed") }
        }
        
        // AMAP (高德地图) - 备用
        maven {
            name = "AMAP"
            url = uri("https://maven.amap.com/maven2/")
            content { includeGroup("com.amap.api") }
        }
        
        // Baidu LBS (百度地图) - 备用
        maven {
            name = "BaiduLBS"
            url = uri("https://jitpack.io")
            content { includeGroup("com.github.baidu.lbsyun") }
        }
    }
}

rootProject.name = "WJFakeLocation"
include(":app")
